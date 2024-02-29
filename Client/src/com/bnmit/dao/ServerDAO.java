package com.bnmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bnmit.util.MySQLUtility;

public class ServerDAO {

	public void register(String username, String password) throws Exception {
		Connection con = null;
		try {
			con = MySQLUtility.getConnection();
			PreparedStatement ps = con.prepareStatement("insert into CLIENT values (?,?)");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			con.close();
		}
	}

	public boolean isValidUser(String username, String password) throws Exception {
		Connection con = null;
		try {
			con = MySQLUtility.getConnection();
			PreparedStatement ps = con
					.prepareStatement("select count(*) from CLIENT where username=? and password=?  ");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			con.close();
		}
	}
}
