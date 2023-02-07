package mvc.controller;

import market.ver01.dao.CartDAO;
import market.ver01.dto.CartDTO;
import mvc.database.DBConnection;
import mvc.model.OrderDAO;
import mvc.model.OrderDataDTO;
import mvc.model.OrderInfoDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        else if (command.contains("pay.do")) { // 주문서 정보 저장 및 결제 수단 출력.
        	
        	setOrderInfo(req); // 주문정보 저장
        	
        	// 장바구니 합계 금액
        	int totalPrice = getTotalPrice(getOrderNo(req));
        	req.setAttribute("totalPrice", totalPrice);
        	
        	// 주문서 정보 가져옴.
        	OrderInfoDTO info = getOrderInfo(getOrderNo(req));
        	req.setAttribute("info", info);
        	
        	// 주문상품 정보 가져오기
        	String orderProductName = getOrderProductName(getOrderNo(req));
        	req.setAttribute("orderProductName", orderProductName);
        	
            req.getRequestDispatcher("/WEB-INF/order/pay.jsp").forward(req, resp);

        	
        }
        
    }
    public OrderInfoDTO getOrderInfo(String orderNo) {
    	OrderInfoDTO dto = new OrderInfoDTO();
    	String sql = "SELECT * FROM ORDER_INFO WHERE orderNo = '" + orderNo + "'";
    	try (Connection conn = DBConnection.getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql);
    		ResultSet resultSet = pstmt.executeQuery();
    	) {
    		if(resultSet.next()) {
    			dto.setOrderNo(resultSet.getString(1));
    			dto.setMemberId(resultSet.getString(2));
    			dto.setOrderName(resultSet.getString(3));
    			dto.setOrderTel(resultSet.getString(4));
    			dto.setOrderEmail(resultSet.getString(5));
    			
    			dto.setReceiveName(resultSet.getString(6));
    			dto.setReceiveTel(resultSet.getString(7));
    			dto.setReceiveAddress(resultSet.getString(8));
    			dto.setPayAmount(resultSet.getInt(9));
    			dto.setPayMethod(resultSet.getString(10));
    			
    			dto.setCarryNo(resultSet.getString(11));
    			dto.setOrderStep(resultSet.getString(12));
    			dto.setDateOrder(resultSet.getString(13));
    			dto.setDatePay(resultSet.getString(14));
    			dto.setDateCarry(resultSet.getString(15));
    			
    			dto.setDateDone(resultSet.getString(16));
    			
    		}
    	} catch (Exception ex) {
    		System.out.println("getOrderInfo() 에러 : " + ex);
    	}
		return dto;
	}
    
    private String getOrderProductName(String orderNo) {
    	OrderDAO dao = OrderDAO.getInstance();
    	return dao.getOrderProductName(orderNo);
    }

//    public String getOrderProdcutName(String orderNo) {
//    	String orderProdcutName = null;
//    	int orderProdcutCnt = 0;
//    	String sql = "SELECT * FROM `order_data` WHERE orderNo = '" + orderNo + "'";
//    	try (Connection conn = DBConnection.getConnection();
//       	     PreparedStatement pstmt = conn.prepareStatement(sql);
//       		 ResultSet resultSet = pstmt.executeQuery();) {
//			while(resultSet.next()) {
//				if(orderProdcutCnt == 0) {
//					orderProdcutName = resultSet.getString("productName");
//				}
//				orderProdcutCnt++;
//			}
//			orderProdcutName += "외 " + (orderProdcutCnt - 1) + "건";
//		} catch (Exception e) {
//			System.out.println("getOrderProdcutName() 에러 : " + e);
//		}
//    	return orderProdcutName;
//    }

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
