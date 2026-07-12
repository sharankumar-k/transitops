package com.transitops.backend.controller;

import com.transitops.backend.dto.ReportDto;
import com.transitops.backend.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public ReportDto.VehicleAnalytics getVehicleAnalytics(@PathVariable Long vehicleId) {
        return reportService.getVehicleAnalytics(vehicleId);
    }

    @GetMapping("/vehicles/csv")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public void downloadVehiclesCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"vehicle-analytics.csv\"");

        List<ReportDto.VehicleAnalytics> analyticsList = reportService.getAllVehicleAnalytics();
        PrintWriter writer = response.getWriter();

        // Write Header
        writer.println("Registration Number,Vehicle Name,Completed Distance,Fuel Liters,Fuel Efficiency,Fuel Cost,Maintenance Cost,Other Expenses,Operational Cost,Revenue,ROI Percentage");

        // Write Rows
        for (ReportDto.VehicleAnalytics v : analyticsList) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(v.getRegistrationNumber()),
                    escapeCsv(v.getVehicleName()),
                    v.getTotalCompletedDistance(),
                    v.getTotalFuelLiters(),
                    v.getFuelEfficiency(),
                    v.getTotalFuelCost(),
                    v.getTotalMaintenanceCost(),
                    v.getTotalOtherExpenses(),
                    v.getTotalOperationalCost(),
                    v.getTotalRevenue(),
                    v.getVehicleROI() != null ? v.getVehicleROI() + "%" : "N/A"
            );
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}