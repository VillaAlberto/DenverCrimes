package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.crimes.model.Event;

public class EventsDao {

	public List<String> listAllCategories() {
		String sql = "SELECT DISTINCT offense_category_id FROM `events`";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			List<String> list = new ArrayList<>();
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(res.getString("offense_category_id"));
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Month> listAllMonths() {
		String sql = "SELECT DISTINCT MONTH(reported_date) FROM `events` ORDER BY MONTH(reported_date)";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			List<Month> list = new ArrayList<>();
			ResultSet res = st.executeQuery();

			while (res.next()) {
				int mese = res.getInt(1);
				LocalDate ld = LocalDate.of(1990, mese, 10);
				list.add(ld.getMonth());
			}
			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> listAllVertex(int mese, String category) {
		String sql = "SELECT DISTINCT offense_type_id FROM events WHERE MONTH(reported_date)=? AND offense_category_id=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			st.setString(2, category);
			List<String> list = new ArrayList<>();
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(res.getString(1));
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Event> listAllEvents() {
		String sql = "SELECT * FROM events";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Event> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public int calcolaPeso(String type1, String type2, int month) {
		String sql = "SELECT COUNT(DISTINCT e1.neighborhood_id) " + "FROM `events` AS e1, `events` AS e2 "
				+ "WHERE e1.neighborhood_id=e2.neighborhood_id AND MONTH(e1.reported_date)=? AND MONTH(e2.reported_date)=? "
				+ "AND e1.offense_type_id=? " + "AND e2.offense_type_id=? "
				+ "GROUP BY e1.offense_type_id AND e2.offense_type_id";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, month);
			st.setInt(2, month);
			st.setString(3, type1);
			st.setString(4, type2);
			ResultSet res = st.executeQuery();

			if (res.next()) {
				conn.close();
				return res.getInt(1);
			}

			return -1;

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
