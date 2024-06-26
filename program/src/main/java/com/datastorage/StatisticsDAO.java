package com.datastorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StatisticsDAO {

    public static ObservableList<String> getGenderStatistics() {
        ObservableList<String> genderStatistics = FXCollections.observableArrayList();
        Connection conn = SQLServerDatabase.getDatabase().getConnection();
        
        try {
            String query = "WITH GenderCounts AS (" +
                    "    SELECT " +
                    "        Gender, " +
                    "        COUNT(r.EmailAddress) AS TotalRegistrations " +
                    "    FROM " +
                    "        Registration r " +
                    "        JOIN Participant p ON r.EmailAddress = p.EmailAddress " +
                    "    GROUP BY " +
                    "        Gender " +
                    "), CertificateCounts AS (" +
                    "    SELECT " +
                    "        p.Gender, " +
                    "        COUNT(c.EmailAddress) AS TotalCertificates " +
                    "    FROM " +
                    "        Certificate c " +
                    "        JOIN Participant p ON c.EmailAddress = p.EmailAddress " +
                    "    GROUP BY " +
                    "        p.Gender " +
                    ")" +
                    "SELECT " +
                    "    gc.Gender, " +
                    "    COALESCE(cc.TotalCertificates, 0) AS TotalCertificates, " +
                    "    gc.TotalRegistrations, " +
                    "    CASE " +
                    "        WHEN gc.TotalRegistrations > 0 THEN " +
                    "            CONVERT(DECIMAL(5, 2), COALESCE(cc.TotalCertificates, 0) * 100.0 / gc.TotalRegistrations) " +
                    "        ELSE 0 " +
                    "    END AS PercentageCertificates " +
                    "FROM " +
                    "    GenderCounts gc " +
                    "    LEFT JOIN CertificateCounts cc ON gc.Gender = cc.Gender";

            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                String genderStatisticsEntry = "Gender: " + rs.getString("Gender") +
                        ", Total Certificates: " + rs.getInt("TotalCertificates") +
                        ", Total Registrations: " + rs.getInt("TotalRegistrations") +
                        ", Percentage Certificates: " + rs.getDouble("PercentageCertificates") + "%";
                genderStatistics.add(genderStatisticsEntry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return genderStatistics;
    }

    public static ObservableList<String> getTopWebcasts() {
        ObservableList<String> topWebcasts = FXCollections.observableArrayList();
        Connection conn = SQLServerDatabase.getDatabase().getConnection();

        try {
            String query = "SELECT TOP 3 ContentItemID, COUNT(ContentItemID) AS AmountViewed " +
                    "FROM AmountSeen " +
                    "WHERE ContentItemID IN (SELECT ContentItemID FROM ContentItem WHERE WebcastTitle IS NOT NULL) " +
                    "GROUP BY ContentItemID";

            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                String webcastEntry = "ContentItemID: " + rs.getString("ContentItemID") +
                        ", View count: " + rs.getString("AmountViewed");
                topWebcasts.add(webcastEntry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topWebcasts;
    }

    public static ObservableList<String> getAgeDistribution() {
        ObservableList<String> ageDistribution = FXCollections.observableArrayList();
        // Aangepaste SQL-query voor SQL Server.
        String query = "SELECT CASE " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 10 AND 19 THEN '10-19' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 20 AND 29 THEN '20-29' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 30 AND 39 THEN '30-39' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 40 AND 49 THEN '40-49' " +
               "ELSE '50+' END AS AgeGroup, COUNT(*) AS Count " +
               "FROM Participant " +
               "GROUP BY CASE " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 10 AND 19 THEN '10-19' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 20 AND 29 THEN '20-29' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 30 AND 39 THEN '30-39' " +
               "WHEN DATEDIFF(year, Birthdate, GETDATE()) BETWEEN 40 AND 49 THEN '40-49' " +
               "ELSE '50+' END";

    
        try (Connection conn = SQLServerDatabase.getDatabase().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String ageGroup = rs.getString("ageGroup");
                int count = rs.getInt("count");
                ageDistribution.add(ageGroup + ": " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ageDistribution;
    }
    
}
