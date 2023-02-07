package mvc.model;

import mvc.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class OrderDAO {
    private static OrderDAO instance;

    public static OrderDAO getInstance() {
        if (instance == null)
            instance = new OrderDAO();
        return instance;
    }

    public void clearOrderData(String orderNo) {
//        주문 번호 기분으로 주문 데이터 삭제
//                중복 등록 방지
        String sql = "DELETE from `webMarketDB`.`order_data` where orderNo=? ";
        try (  Connection conn = DBConnection.getConnection();
            PreparedStatement  pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, orderNo);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println("deleteRipple() 에러 : " + ex);
        }
    }

    public boolean insertOrderData(OrderDataDTO dto) {
        int flag = 0;
        String sql = "INSERT INTO `order_data` values(null, ?,?,?,?,?,?,?)";
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, dto.getOrderNo());
            pstmt.setInt(2, dto.getCartId());
            pstmt.setString(3, dto.getProductId());
            pstmt.setString(4, dto.getProductName());
            pstmt.setInt(5, dto.getUnitPrice());
            pstmt.setInt(6, dto.getCnt());
            pstmt.setInt(7, dto.getSumPrice());
            flag = pstmt.executeUpdate();
        }catch (Exception ex ){
            System.out.println("insertOrderData() 에러 : " + ex);
        }
        return flag != 0;
    }

    public void clearOrderInfo(String orderNo) {
		/* 주문번호 기준으로 주문 정보 데이터 삭제.
		 중복 등록 방지.
		 */
    	String sql = "DELETE FROM `ORDER_INFO` WHERE orderNo = ?";
    	try (Connection conn = DBConnection.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, orderNo);
    		pstmt.executeUpdate();
    	} catch (Exception ex) {
    		System.out.println("clearOrderInfo() 에러 : " + ex);
    	}
	}
    
    public String getOrderProductName(String orderNo) {
    	String orderProdcutName = null;
    	int orderProdcutCnt = 0;
    	String sql = "SELECT * FROM `order_data` WHERE orderNo = '" + orderNo + "'";
    	try (Connection conn = DBConnection.getConnection();
       	     PreparedStatement pstmt = conn.prepareStatement(sql);
       		 ResultSet resultSet = pstmt.executeQuery();) {
			while(resultSet.next()) {
				if(orderProdcutCnt == 0) {
					orderProdcutName = resultSet.getString("productName");
				}
				orderProdcutCnt++;
			}
			orderProdcutName += "외 " + (orderProdcutCnt - 1) + "건";
		} catch (Exception e) {
			System.out.println("getOrderProdcutName() 에러 : " + e);
		}
    	return orderProdcutName;
    }
    
    public boolean insertOrderInfo(OrderInfoDTO dto) {
    	int flag = 0;
    	String sql = "INSERT INTO `order_info` VALUES (?, ?, ?, ?, ? " +
    			 "?, ?, ?, ?, ? " +
    			 "?, ?, now(), ?, ?, ?)";
    	try (Connection conn = DBConnection.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, dto.getOrderNo());
    		pstmt.setString(2, dto.getMemberId());
    		pstmt.setString(3, dto.getOrderName());
    		pstmt.setString(4, dto.getOrderTel());
    		pstmt.setString(5, dto.getOrderEmail());
    		
    		pstmt.setString(6, dto.getReceiveName());
    		pstmt.setString(7, dto.getReceiveTel());
    		pstmt.setString(8, dto.getReceiveAddress());
    		pstmt.setInt(9, dto.getPayAmount());
    		pstmt.setString(10, dto.getPayMethod());
    		
    		pstmt.setString(11, dto.getCarryNo());
    		pstmt.setString(12, "orderFail");
    		pstmt.setString(13, dto.getDatePay());
    		pstmt.setString(14, dto.getDateCarry());
    		
    		pstmt.setString(15, dto.getDateDone());
    		
    		flag = pstmt.executeUpdate();
    	} catch (Exception ex) {
	        System.out.println("insertOrderInfo() 에러 : " + ex);
	    }
    	return flag != 0;
    }
}
