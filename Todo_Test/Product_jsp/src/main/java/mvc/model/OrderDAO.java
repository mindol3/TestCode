package mvc.model;

import mvc.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;


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
}
