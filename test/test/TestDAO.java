import dao.TMDao;

public class TestDAO {

    public static void main(String[] args) {
        TMDao tmdao = new TMDao();
        System.out.println(tmdao.verfiyHavasNetwork(4022, "Kia_ww"));
    }

}
