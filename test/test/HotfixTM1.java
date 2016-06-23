import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotfixTM1 {

    public static void main(String[] args) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;
        PreparedStatement stmt3 = null;

        con = DBConnection.getDBConnection();

        String sql = "select camId,ancId from Campaign c, CampaignEmailList cel where c.camId=cel.campaign_id";

        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            String sql2 = "select distinct u.usrEmail from UserAnnonceur ua,Pole p,BuyerPole bp,UserT u where p"
                    + ".poleId=bp.poleId and u.usrLogin=bp.usrLogin and ua.usrId= u.usrId and (ua.typeAlert=20 or ua"
                    + ".typeAlert=1) and p.pol in ('TCH','TJS','TAP') and ua.ancId=?;";
            String sql3 = "update CampaignEmailList set email_list=? where campaign_id=?";

            while (rs.next()) {

                stmt2 = con.prepareStatement(sql2);
                stmt2.setInt(1, rs.getInt("ancId"));
                rs2 = stmt2.executeQuery();
                String emailList = "";
                while (rs2.next()) {
                    emailList = emailList + rs2.getString("usrEmail");
                }
                System.out.println("emailList: " + emailList);
                stmt3 = con.prepareStatement(sql3);
                stmt3.setString(1, emailList);
                stmt3.setInt(2, rs.getInt("camId"));
                stmt3.executeUpdate();
            }
            System.out.println("fininshed...");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                rs2.close();
                stmt.close();
                stmt2.close();
                stmt3.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
