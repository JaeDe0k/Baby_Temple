package com.babytemple.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import com.babytemple.window.Initial_Settings;

public class BT_USER_DB {
	private Connection conn; // db�� �������ְ� �ϴ� ��ü
	private PreparedStatement pstmt;
	private ResultSet rs;
	private Initial_Settings Initial = new Initial_Settings();;

	// mysql ����
	public BT_USER_DB() {
		try {
			String dbURL = "jdbc:mysql://localhost:3306/BT"; // DB�ּ�
			String dbID = "root";
			String dbPassword = "root";
			Class.forName("com.mysql.jdbc.Driver"); // JDBC ����̹� �ε�
			conn = (Connection) DriverManager.getConnection(dbURL, dbID, dbPassword); // �ڹ�-DB���� �����۾� (����URL,����,��й�ȣ)
		} catch (Exception e) {}

	}

	// �α��� �õ�
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword, userIcon FROM USER WHERE userID = ?"; // userID�� ��ġ�ϸ� �ش��ϴ� userPassword�� ��ȸ
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL); // SQL��¯�� DB�� ����
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery(); //SQL ���ޱ�
			if (rs.next()) { //�ش� ���̵� ������ �����Ѵ�.
				if (rs.getString(1).equals(userPassword)) { //�ش� ���̵�� �Է��� ��й�ȣ�� ��ġ�ϸ�
					int iconNum = rs.getInt("userIcon");
					return iconNum; // ���̵𼺰�
				} else
					return -2; // ���Ʋ��
			}
			return -1; // ���̵� ������
		} catch (Exception e) {}
		return -3; // ��� ���ӿ���
	}

	// ���Խõ�
	public int join(String userID, String userPassword) {
		String SQL = "SELECT userID FROM USER WHERE userID = ?"; //userID�� ������ �ش� userID��ȸ
		try {
		pstmt = (PreparedStatement) conn.prepareStatement(SQL); // SQL�� �ۼ�
		pstmt.setString(1, userID);
		rs = pstmt.executeQuery(); //SQL ���ޱ�
		if(!rs.next()) { //���� �ش� userID�� ��ȸ���� ������ ����
			SQL = "INSERT INTO USER VALUES(?,?,?)"; //user���̺� ��2���� ������� �߰��ϴ� ��ɹ�
			pstmt = (PreparedStatement) conn.prepareStatement(SQL); //SQL�� �ۼ�
			pstmt.setString(1, userID);
			pstmt.setString(2, userPassword);
			pstmt.setInt(3, 0); //�⺻ ������ 0���� ����
			pstmt.executeUpdate(); //SQL�� ����
			return 1; //���Լ���
			} else
				return -1; // ���Խ���
		}catch(SQLException e) {}
		return -3; // DB���ӿ���
	}
	
	//��ȣã��
	public String pwFind(String userID) {
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?"; //�ش� userID�� �����ϸ� userPassword ���
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL); // SQL�� �ۼ�
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery(); //SQL ���ޱ�
		if(rs.next()) {
			return rs.getString(1); // ��ȣã�⼺�� �� �ش� userPassword �� ��ȯ
		} else
			return "�̰��� �г���";
		}catch(SQLException e) {}
		return "DB����"; // DB���ӿ���
	}
	
	public String[] selectRank(int stage, String id) {
		String SQL = "SELECT P1,P2,ClearTime, ROW_NUMBER() OVER (ORDER BY ClearTime ASC) RANKs FROM Ranking WHERE Stage=?;";
		String RankList[] = new String[12];
		boolean full;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			pstmt.setInt(1, stage);
			rs = pstmt.executeQuery(); //SQL ���ޱ�
			for(int i=0;rs.next();i+=2) {
				if(i<10) {
					RankList[i] = rs.getString("ClearTime");
					RankList[i+1] = rs.getString("P1")+", "+rs.getString("P2");
				}
				
				if((rs.getString("P1").equals(id) || rs.getString("P2").equals(id)) && RankList[10] == null) {
					RankList[10] = rs.getString("RANKs");
					RankList[11] = rs.getString("ClearTime");
				}
				
				full = Arrays.stream(RankList).allMatch(Objects::nonNull);
				if(full)
					break;
			}
			return RankList;
		} catch (SQLException e) {}
		return null;
	}
	
	//��ŷ������Ʈ
	public int sendRank(String P1, String P2, String ClearTime, int Stage) {
		String SQL = "insert into Ranking (P1,P2,ClearTime,Stage) values(?,?,?,?)";
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			pstmt.setString(1, P1);
			pstmt.setString(2, P2);
			pstmt.setString(3, ClearTime);
			pstmt.setInt(4, Stage);
			pstmt.executeUpdate(); //SQL�� ����
			
		} catch (SQLException e) {}
		return -3;
	}
	
	//������ ����
	public void setIcon(String userID, int userIcon) {
		String SQL = "update user set userIcon = ? where userID = ? ";
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			pstmt.setInt(1, userIcon);
			pstmt.setString(2, userID);
			pstmt.executeUpdate(); //SQL�� ����
			
		} catch (SQLException e) {}
	}
	
	public void close() {
			try {
				if(rs!=null) rs.close();
                if(pstmt!=null) pstmt.close();
                if(conn!=null) conn.close();
			} catch (SQLException e) {
				System.exit(0);
			}
	}
}