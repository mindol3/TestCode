package mvc.controller;

import market.ver01.dao.CartDAO;
import market.ver01.dto.CartDTO;
import mvc.model.OrderDAO;
import mvc.model.OrderDataDTO;
import mvc.model.OrderInfoDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/web_jsp/market/order/*")
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String RequestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String command = RequestURI.substring(contextPath.length());

        resp.setContentType("text/html; charset=utf-8");
        req.setCharacterEncoding("utf-8");

        System.out.println("command : " + command);

        if (command.contains("/form.do")) {//주문서 / 배송 정보 입력 페이지
        	//상단에 장바구니 출력
            setOrderData(req);
            
            // 상단에 출력할 장바구니 목록
            ArrayList<OrderDataDTO> datas = getOrderData(getOrderNo(req));
            req.setAttribute("datas", datas);
            
            //장바구니 합계 금액
            int totalPrice = getTotalPrice(getOrderNo(req));
            req.setAttribute("totalPrice", totalPrice);
            
            
            req.getRequestDispatcher("/WEB-INF/order/form.jsp").forward(req, resp);
        }
        
        else if (command.contains("success.do")) { //결제 승인이 정상적으로 된 경우
        	// 새로 고침시에 결제 승인 API를 재승인 요청을 해서 오류 메시지가 나올수 있으니
        	// 처리된 후에는 sendRedirect
        	try {
        		processSuccess(req); // 처리
        		
        		resp.sendRedirect("/order/orderDone.do");
        		
        	} catch (Exception e) {
        		throw new RuntimeException(e);
        	}
        }
        else if (command.contains("orderDone.do")) { //결제 완료
        	// order_date에 있는 cartId 기준으로 장바구니에 있는 상품을 삭제
        	deleteCartWhenOrderDone(getOrderNo(req));
        	
        	// 상단에 출력할 장바구니 목록
        	ArrayList<OrderDateDTO> datas = getOrderData(getOrderNo(req));
        	req.setAttribute("datas", datas);
        	
        	// 주문 정보 가져옴.
        	OrderInfoDTO info = getOrderInfo(getOrderNo(req));
        	// 주문 단계를 한글로
        	OrderStep orderstep = OrderStep.valueOf(info.getOrderStep());
        	info.setOrderStep(orderstep.getStep());
        	req.setAttribute("info", info);
        	
        	req.getRequestDispatcher("/WEB_INF/order/orderDone.jsp").forward(req, resp);
        }
    }
      
    private void deleteCartWhenOrderDone(String orderNo) {
    	/*주문 처리가 완료된 후 order_dta에 있는 cartId 기준으로 장바구니를 삭제*/
    	OrderDAO orderDAO = OrderDAO.getInstance();
    	CartDAO cartDAO = new CartDAO();
    	
    	ArrayList<OrderDataDTO> dtos = orderDAO.selectAllOrderData(orderNo);
    	for (OrderDataDTO dto : dtos) {
    		try {
    			cartDAO.deleteCartById(orderNo, dto.getCartId());
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}
    }
        	
        
    private void setOrderInfo(HttpServletRequest request) {
    	OrderDAO dao = OrderDAO.getInstance();
    	
    	// 1. 중복을 막기 위해 주문번호로 저장된 데이터 삭제
    	dao.clearOrderInfo(getOrderNo(request));
    	
    	// 2. request은 값을 dto에 저장해서 dao에 전달
    	
    	OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
    	
    	orderInfoDTO.setOrderNo(getOrderNo(request));
    	orderInfoDTO.setMemberId(getMemberId(request));
    	orderInfoDTO.setOrderName(request.getParameter("orderName"));
    	orderInfoDTO.setOrderTel(request.getParameter("orderTel"));
    	orderInfoDTO.setOrderEmail(request.getParameter("orderEmail"));
    	orderInfoDTO.setReceiveName(request.getParameter("receiveName"));
    	orderInfoDTO.setReceiveTel(request.getParameter("receiveTel"));
    	orderInfoDTO.setReceiveAddress(request.getParameter("receiveAddress"));
    	orderInfoDTO.setPayAmount(getTotalPrice(getOrderNo(request)));
    	
    	dao.insertOrderInfo(orderInfoDTO);
    	
    }
    
    private String getMemberId(HttpServletRequest request) {
    	/*세션에 저장된 아이디 가져옴*/
    	HttpSession session = request.getSession();
    	return (String) session.getAttribute("sessionMemberId");
    }
    
    


    

	private String getOrderNo(HttpServletRequest req) {
        /*주문 번호 반환
         * 1. 주문번호 사용 때문에 코드 반복이 되어서
         * 2. 주문번호 체계가 변할 경우를 대비해 메서드화*/
        HttpSession session = req.getSession();
        return session.getId();
    }

    private void procesSuccess(HttpServletRequest request) throws Exception {
    	// 결제가 정상적으로 끝난 경우 호출
    	// 결제 시도 페이지의 payMentData 객체의 successUrl 속성으로 접근
    	// 접근시에 orderId, paymentKey, amount 파라미터 만으로 접근 (주의 : 접근 url에는 성공여부등의 정보는 포함되어 있지 않음)
    	
    	// 1. 파라미터 정리
    	// paymentKey: 결제의 키 값
    	// orderId : 주문 ID입니다. 결제창을 열 때 requestPayment()에 담아 보낸 값
    	// amount : 실제로 결제된 금액.
    	String orderId = request.getParameter("orderId");
    	System.out.println("orderId :" +orderId);
    	String paymentKey = request.getParameter("paymentKey");
    	System.out.println("paymentKey :" + paymentKey);
    	String amount = request.getParameter("amount");
    	System.out.println(" :" + amount);
    	
    	  // 2. 토스에서 미리 받은 상점의 secretKey를 사용. 토스쪽에서는 해당 값으로 상점을 구분
    	  String secretKey = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R" + ":";
    	  
    	  // secretKey를 인코딩
		  Encoder encoder = Base64.getEncoder(); 
		  byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
		  String authorizations = "Basic "+ new String(encodedBytes, 0, encodedBytes.length);
		  
		  // 3. 토스 결제 승인 API 호출하기
		  // REST API 방식으로 처리
		  
		  // 접근 url에 paymentKey 포함
		  
		  URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);
		  
		  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		  // secretKey를 포함
		  connection.setRequestProperty("Authorization", authorizations);
		  connection.setRequestProperty("Content-Type", "application/json");
		  connection.setRequestMethod("POST");
		  connection.setDoOutput(true);
	
		  JSONObject obj = new JSONObject();
		  obj.put("orderId", orderId);
		  obj.put("amount", amount);
		  
		  OutputStream outputStream = connection.getOutputStream();
		  outputStream.write(obj.toString().getBytes("UTF-8"));
		  // 호출 후 결과 코드 가져오기
		  int code = connection.getResponseCode();
		  
		  // 4. 결과 코드로 이후 결과 처리
		  // code가 200번대이면 성공으로 처리
		  boolean isSuccess = code >= 200 && code < 300 ? true : false;
		  System.out.println("isSuccess :" + isSuccess);
		  
		  InputStream responseStream = isSuccess? connection.getInputStream(): connection.getErrorStream();
		  Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

		  JSONParser parser = new JSONParser();
		  // response 값을 jsonObject로 저장.
		  JSONObject jsonObject = (JSONObject) parser.parse(reader);
		  System.out.println("결과 데이터:" + jsonObject.toJSONString());
		  responseStream.close();
		  
		  if (isSuccess) { // 성공시 처리
			  System.out.println("주문번호 orderId : " + jsonObject.get("orderId"));
			  System.out.println("결제방법 method : " + jsonObject.get("method"));
			  System.out.println("결제승인일시 approvedAt : " + jsonObject.get("approvedAt"));
			  // 각각의 결제방법에 따라 처리. 보통은 가상계좌와 기타 결제 수단으로 나누어짐
			  String method = (String) jsonObject.get("method");
			  
			  OrderInfoDTO dto = new OrderInfoDTO();
			  dto.setOrderNo((String) jsonObject.get("orderId")); // 주문번호
			  dto.setPayMethod(method);
			  
			  if(method.equals("가상계좌")) {
				  dto.setOrderStep(String.valueOf(OrderStep.orderReceive));
			  }
			  else {
				  dto.setOrderStep(String.valueOf(OrderStep.payReceive));
				  dto.setDatePay((String) jsonObject.get("approvedAt")); // 입금일시
			  }
			 // processSuccessUpdate(dto);
		  }
	    }
    
    private boolean processSuccessUpdate(OrderInfoDTO dto) {
    	// dto 기준으로 주문 정보 업데이트 실행
    	OrderDAO dao = OrderDAO.getInstance();
    	return dao.updateOrderInfoWhenProcessSuccess(dto);
    }

    private String getOrderNo(HttpServletRequest req) {
        /*주문 번호 반환
         * 1. 주문번호 사용 때문에 코드 반복이 되어서
         * 2. 주문번호 체계가 변할 경우를 대비해 메서드화*/
        HttpSession session = req.getSession();
        return session.getId();
    }

    private void setOrderData(HttpServletRequest request) {
        /* 장바구니에 있는 상품을 주문데이터에 복사
        결제 금액을 장바구니가 아니라 주문데이터 기준으로 계산.
        */
        OrderDAO dao = OrderDAO.getInstance();
        // 주문 번호 가져오기
        String orderNo = getOrderNo(request);
        System.out.println("orderNo = " + orderNo);
        // 1. 중복을 막기 위해 주문번호로 저장된 데이터 삭제
        dao.clearOrderData(orderNo);
        // 2. 주문번호 기준으로 장바구니에 있는 상품을 가지고 옴
        CartDAO cartDAO = new CartDAO();
        ArrayList<CartDTO> carts = cartDAO.getCartList(orderNo);
        System.out.println("carts = " + carts);
        // 3. CartList를 OrderData List로 변경
        ArrayList<OrderDataDTO> dtos= changeCartData(carts, orderNo);
        System.out.println("dtos = " + dtos);

        // 4. OrderData List를 데이터 베이스에 저장
        for(OrderDataDTO dto : dtos) {
            dao.insertOrderData(dto);
        }
    }

    private ArrayList<OrderDataDTO> changeCartData(ArrayList<CartDTO> carts, String orderNo) {
        ArrayList<OrderDataDTO> datas = new ArrayList<>();
        for(CartDTO cart : carts){
            OrderDataDTO dto = new OrderDataDTO();
            dto.setOrderNo(orderNo);
            dto.setCartId(cart.getP_cartId());
            dto.setProductId(cart.getP_productId());
            dto.setProductName(cart.getP_name());
            dto.setUnitPrice(cart.getP_unitPrice());
            dto.setCnt(cart.getP_cnt());
            dto.setSumPrice(cart.getP_unitPrice() * cart.getP_cnt());
            datas.add(dto);
        }
        return datas;
    }

//    private ArrayList<OrderDataDTO> getOrderData(String orderNo) {
//    }

}
