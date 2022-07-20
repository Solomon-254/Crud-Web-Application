
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author User
 */
@ManagedBean(name = "user")
@RequestScoped

public class User {

    /**
     * Creates a new instance of User
     */
    public User() {
    }
    //Creating Entity variables
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    //Creating arraylist that will hold User objects with their respective fields
    ArrayList memberList;
    //Connection from the sql package
    Connection c = null;

    private final Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

    //Setters and Getter method start from here
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //--------------------------
    //Connecting to databases
    public Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/mspace", "root", "35318756sl");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }
        return c;
    }

    //Fetching records from the users mysql database
    public ArrayList memberList() {
        try {
            memberList = new ArrayList();
            c = getConnection();
            Statement stamt = getConnection().createStatement();
            ResultSet res = stamt.executeQuery("select*from users");
            while (res.next()) {
                User user = new User();
                user.setId(res.getInt("id"));
                user.setFirstName(res.getString("firstName"));
                user.setLastName(res.getString("lastName"));
                user.setEmail(res.getString("email"));
                user.setPassword(res.getString("password"));

                memberList.add(user);
            }
            c.close();
        } catch (SQLException e) {
            e.getMessage();
        }

        return memberList;

    }

    //Save Method to insert data into the database
    public String saveMember() {
        int result = 0;
        try {
            c = getConnection();
            String query = "insert into users(firstName,lastName,email,password) values(?,?,?,?)";
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);

            result = preparedStatement.executeUpdate();
            c.close();

        } catch (SQLException e) {
            System.out.println("Something is wrong with your backend code");
        }

        if (result != 0) {
            return "index.xhtml?faces-redirect=true";
        } else {
            return "createMembers.xhtml?faces-redirect=true";
        }
    }

    //Fetching Members records to the input textfield
    public String editMember(int id) {
        User user = null;
        System.out.println(id);
        try {
            c = getConnection();
            Statement stmt = getConnection().createStatement();
            ResultSet res = stmt.executeQuery("select * from users where id = " + (id));
            res.next();

            user = new User();
            user.setId(res.getInt("id"));
            user.setFirstName(res.getString("firstName"));
            user.setLastName(res.getString("lastName"));
            user.setEmail(res.getString("email"));
            user.setPassword(res.getString("password"));

            map.put("editMember", user);
            c.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "/editMembers.xhtml?faces-redirect=true";
    }

    //Updating member records
    public String updateMember(User user) {
        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement(
                    "update users set firstName=?,lastName=?,email=?,password=?");
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.firstName);
            stmt.setString(3, user.lastName);
            stmt.setString(4, user.email);
            stmt.setString(5, user.password);
            c.close();
            stmt.executeUpdate();
        } catch (SQLException e) {

        }
        return "/index.xhtml?faces-redirect=true";
    }

    //Deleting a member
    public void deleteMember(int id) {
        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement("delete from users where id = " + id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);

        }
    }

}
