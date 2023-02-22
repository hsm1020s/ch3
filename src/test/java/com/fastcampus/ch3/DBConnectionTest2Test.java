package com.fastcampus.ch3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/**/root-context.xml"})
public class DBConnectionTest2Test {
    final int FAIL = 0;
    @Autowired
    DataSource ds;
    @Test
    public void insertUserTest()throws Exception{
        User user = new User("asadaf","1234","mhs","aaaa@aaa.com", new Date(), "fb",new Date());
        deleteAll();
        int rowCnt = insertUser(user);

        assertTrue(rowCnt==1);
    }
@Test
public void selectUserTest() throws Exception{
    deleteAll();
    User user = new User("asadaf","1234","mhs","aaaa@aaa.com", new Date(), "fb",new Date());
    int rowCnt = insertUser(user);

    User user2=  selectUser("asadaf");
    assertTrue(user.getId().equals("asadaf"));
}
@Test
public void deleteUserTest() throws Exception{
        deleteAll();
    int rowCnt =  deleteUser("asadaf");

    assertTrue(rowCnt==0);

    User user = new User("asadaf","1234","mhs","aaaa@aaa.com", new Date(), "fb",new Date());
     rowCnt = insertUser(user);
    assertTrue(rowCnt==1);
    rowCnt =  deleteUser(user.getId());
    assertTrue(rowCnt==1);

    assertTrue(selectUser(user.getId())==null);
}
//매개변수로 받은 사용자 정보로 user_info 테이블을 업데이트 하는 메서드
public int updateUser(User user) throws Exception{
    int rowCnt = FAIL; //  insert, delete, update

    String sql = "update user_info " +
            "set pwd = ?, name=?, email=?, birth =?, sns=?, reg_date=? " +
            "where id = ? ";
    // try-with-resources - since jdk7
    try (
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격, 성능향상
    ){
        pstmt.setString(1, user.getPwd());
        pstmt.setString(2, user.getName());
        pstmt.setString(3, user.getEmail());
        pstmt.setDate(4, new java.sql.Date(user.getBirth().getTime()));
        pstmt.setString(5, user.getSns());
        pstmt.setTimestamp(6, new java.sql.Timestamp(user.getReg_date().getTime()));
        pstmt.setString(7, user.getId());

        rowCnt = pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        return FAIL;
    }
    return rowCnt;

}

public int deleteUser(String id) throws Exception{
    Connection conn = ds.getConnection();
    String sql="delete from user_info where id= ? ";

    PreparedStatement pstmt= conn.prepareStatement(sql); // SQL Injection공격,성능향상
    pstmt.setString(1,id);
 //   int rowCnt= pstmt.executeUpdate(); // insert , delete , update
//    return rowCnt;
    return pstmt.executeUpdate(); // insert , delete , update

}

    public User selectUser(String id)throws Exception{
        Connection conn = ds.getConnection();

        String sql="select * from user_info where id= ? ";

        PreparedStatement pstmt= conn.prepareStatement(sql); // SQL Injection공격,성능향상
        pstmt.setString(1,id);
        ResultSet rs =  pstmt.executeQuery(); // select 문 일때

        if(rs.next()){
            User user = new User();
            user.setId(rs.getString(1));
            user.setPwd(rs.getString(2));
            user.setName(rs.getString(3));
            user.setEmail(rs.getString(4));
            user.setBirth(new Date(rs.getDate(5).getTime()));
            user.setSns(rs.getString(6));
            user.setReg_date(new Date(rs.getTimestamp(7).getTime()));
            return user;
        }
    return null;
    }

    private void deleteAll() throws Exception{
        Connection conn = ds.getConnection();

        String sql="delete from user_info";

        PreparedStatement pstmt= conn.prepareStatement(sql); // SQL Injection공격,성능향상
        pstmt.executeUpdate(); // insert , delete , update

    }

    @Test
    public void transactionTest() throws Exception{
        Connection conn=null;
        try {
            deleteAll();
            conn = ds.getConnection();
            conn.setAutoCommit(false); //conn.setAutoCommit(true)이기때문에

//        insert into user_info (id, pwd, name, email, birth, sns, reg_date)
//        values ('asdf22','1234','mhs','aaa@aaa.com','2021-01-01','insta',now());

            String sql="insert into user_info values (?, ?, ?, ?, ?, ?, now())";

            PreparedStatement pstmt= conn.prepareStatement(sql); // SQL Injection공격,성능향상
            pstmt.setString(1,"asdf");
            pstmt.setString(2,"1234");
            pstmt.setString(3,"abc");
            pstmt.setString(4,"aaa@aaa.com");
            pstmt.setDate(5, new java.sql.Date (new Date().getTime()) ) ;
            pstmt.setString(6,"fb");

            int rowCnt = pstmt.executeUpdate();

            pstmt.setString(1,"asdf");
            rowCnt = pstmt.executeUpdate();

            conn.commit();

        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
        }


    }
    //사용자 정보를 user_info 테이블에 저장하는 메서드
    public int insertUser(User user)throws Exception{
        Connection conn = ds.getConnection();

//        insert into user_info (id, pwd, name, email, birth, sns, reg_date)
//        values ('asdf22','1234','mhs','aaa@aaa.com','2021-01-01','insta',now());

        String sql="insert into user_info values (?, ?, ?, ?, ?, ?, now())";

        PreparedStatement pstmt= conn.prepareStatement(sql); // SQL Injection공격,성능향상
        pstmt.setString(1,user.getId());
        pstmt.setString(2,user.getPwd());
        pstmt.setString(3,user.getName());
        pstmt.setString(4,user.getEmail());
        pstmt.setDate(5, new java.sql.Date (user.getBirth().getTime()) ) ;
        pstmt.setString(6,user.getSns());

        int rowCnt = pstmt.executeUpdate(); // insert , delete , update

    return rowCnt;
    }

    @Test
    public void main() throws Exception {
//        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
//        DataSource ds = ac.getBean(DataSource.class);

        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.

        System.out.println("conn = " + conn);
         assertTrue(conn!=null);//괄호 안의 조건식이 true면 성공 아니면 실패
    }
}