/* ==========================================================
   LOGISTICS EXCEPTION & DELIVERY RESOLUTION PLATFORM
   SLA & ANALYTICS MODULE
   File: js/analytics.js
   ========================================================== */

(() => {
    "use strict";

    const API_BASE = "http://localhost:8080";

    const API_ENDPOINTS = {
        shipments: "/api/shipments",
        exceptions: "/api/exceptions",
        resolutions: "/api/resolutions"
    };

    const state = {
        shipments: [],
        exceptions: [],
        resolutions: [],
        loaded: false
    };

    document.addEventListener("DOMContentLoaded", initializeAnalytics);

    async function initializeAnalytics() {
        console.log("Initializing SLA & Analytics module...");

        try {
            await loadAnalyticsData();
        } catch (error) {
            console.error("Analytics initialization error:", error);
            showMessage(
                "Analytics data could not be loaded. Check your backend connection and authentication."
            );
        }
    }

    /* ======================================================
       API REQUESTS
       ====================================================== */

    function getAuthHeaders() {
        const token =
            localStorage.getItem("token") ||
            localStorage.getItem("authToken") ||
            localStorage.getItem("jwtToken");

        const headers = {
            "Accept": "application/json"
        };

        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }

        return headers;
    }

    async function fetchApi(endpoint) {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "GET",
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            throw new Error(
                `${endpoint} returned HTTP ${response.status}`
            );
        }

        return response.json();
    }

    async function loadAnalyticsData() {
        const results = await Promise.allSettled([
            fetchApi(API_ENDPOINTS.shipments),
            fetchApi(API_ENDPOINTS.exceptions),
            fetchApi(API_ENDPOINTS.resolutions)
        ]);

        state.shipments = results[0].status === "fulfilled"
            ? normalizeArray(results[0].value)
            : [];

        state.exceptions = results[1].status === "fulfilled"
            ? normalizeArray(results[1].value)
            : [];

        state.resolutions = results[2].status === "fulfilled"
            ? normalizeArray(results[2].value)
            : [];

        state.loaded = true;

        console.log("Analytics data:", {
            shipments: state.shipments.length,
            exceptions: state.exceptions.length,
            resolutions: state.resolutions.length
        });

        renderAnalytics();

        const failures = results.filter(
            result => result.status === "rejected"
        );

        if (failures.length > 0) {
            showMessage(
                "Some analytics data could not be loaded. " +
                "Check the backend API endpoints and authentication."
            );
        } else {
            hideMessage();
        }
    }

    function normalizeArray(data) {
        if (Array.isArray(data)) {
            return data;
        }

        if (data && Array.isArray(data.content)) {
            return data.content;
        }

        if (data && Array.isArray(data.data)) {
            return data.data;
        }

        return [];
    }

    /* ======================================================
       ANALYTICS CALCULATIONS
       ====================================================== */

    function calculateMetrics() {
        const totalShipments = state.shipments.length;

        const delivered = state.shipments.filter(shipment =>
            normalizeStatus(shipment.status) === "delivered"
        ).length;

        const inTransit = state.shipments.filter(shipment =>
            ["in transit", "in_transit", "transit"].includes(
                normalizeStatus(shipment.status)
            )
        ).length;

        const delayed = state.shipments.filter(shipment =>
            normalizeStatus(shipment.status).includes("delay")
        ).length;

        const totalExceptions = state.exceptions.length;

        const openExceptions = state.exceptions.filter(exception =>
            ["open", "pending", "new"].includes(
                normalizeStatus(exception.status)
            )
        ).length;

        const resolvedExceptions = state.exceptions.filter(exception =>
            ["resolved", "closed", "completed"].includes(
                normalizeStatus(exception.status)
            )
        ).length;

        const slaViolations = state.exceptions.filter(exception => {
            const type = normalizeStatus(exception.exceptionType);
            const status = normalizeStatus(exception.status);

            return (
                type.includes("sla") ||
                type.includes("breach") ||
                type.includes("late") ||
                type.includes("delay") ||
                status.includes("sla violation")
            );
        }).length;

        const compensation = state.resolutions.reduce(
            (total, resolution) => {
                const amount = Number(
                    resolution.compensationAmount ??
                    resolution.compensation ??
                    resolution.amount ??
                    0
                );

                return total + (Number.isFinite(amount) ? amount : 0);
            },
            0
        );

        const deliveryRate = totalShipments > 0
            ? (delivered / totalShipments) * 100
            : 0;

        const resolutionRate = totalExceptions > 0
            ? (resolvedExceptions / totalExceptions) * 100
            : 0;

        return {
            totalShipments,
            delivered,
            inTransit,
            delayed,
            totalExceptions,
            openExceptions,
            resolvedExceptions,
            slaViolations,
            compensation,
            deliveryRate,
            resolutionRate
        };
    }

    function normalizeStatus(value) {
        return String(value || "")
            .trim()
            .toLowerCase()
            .replace(/-/g, " ")
            .replace(/\s+/g, " ");
    }

    /* ======================================================
       RENDER ANALYTICS
       ====================================================== */

    function renderAnalytics() {
        const metrics = calculateMetrics();

        updateFirstExisting([
            "#totalShipments",
            "#totalShipmentsValue",
            "[data-analytics='total-shipments']"
        ], metrics.totalShipments);

        updateFirstExisting([
            "#deliveredShipments",
            "#deliveredValue",
            "[data-analytics='delivered']"
        ], metrics.delivered);

        updateFirstExisting([
            "#inTransitShipments",
            "#inTransitValue",
            "[data-analytics='in-transit']"
        ], metrics.inTransit);

        updateFirstExisting([
            "#delayedShipments",
            "#delayedValue",
            "[data-analytics='delayed']"
        ], metrics.delayed);

        updateFirstExisting([
            "#totalExceptions",
            "#totalExceptionsValue",
            "[data-analytics='total-exceptions']"
        ], metrics.totalExceptions);

        updateFirstExisting([
            "#openExceptions",
            "#openExceptionsValue",
            "[data-analytics='open-exceptions']"
        ], metrics.openExceptions);

        updateFirstExisting([
            "#resolvedExceptions",
            "#resolvedExceptionsValue",
            "[data-analytics='resolved-exceptions']"
        ], metrics.resolvedExceptions);

        updateFirstExisting([
            "#slaViolations",
            "#slaViolationsValue",
            "[data-analytics='sla-violations']"
        ], metrics.slaViolations);

        updateFirstExisting([
            "#totalCompensation",
            "#compensationValue",
            "[data-analytics='compensation']"
        ], formatCurrency(metrics.compensation));

        updateFirstExisting([
            "#deliveryRate",
            "#deliveryRateValue",
            "[data-analytics='delivery-rate']"
        ], `${metrics.deliveryRate.toFixed(1)}%`);

        updateFirstExisting([
            "#resolutionRate",
            "#resolutionRateValue",
            "[data-analytics='resolution-rate']"
        ], `${metrics.resolutionRate.toFixed(1)}%`);

        renderShipmentTable();
        renderExceptionTable();
        renderCharts(metrics);
    }

    function updateFirstExisting(selectors, value) {
        for (const selector of selectors) {
            const element = document.querySelector(selector);

            if (element) {
                element.textContent = value;
                return;
            }
        }
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 2
        }).format(amount);
    }

    /* ======================================================
       SHIPMENT ANALYTICS TABLE
       ====================================================== */

    function renderShipmentTable() {
        const tableBody =
            document.querySelector("#analyticsShipmentTable tbody") ||
            document.getElementById("analyticsShipmentTableBody");

        if (!tableBody) {
            return;
        }

        if (state.shipments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No shipment data available.
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = state.shipments.map(shipment => {
            const id = escapeHTML(
                shipment.shipmentId ??
                shipment.id ??
                "-"
            );

            const status = escapeHTML(
                shipment.status ?? "Unknown"
            );

            const origin = escapeHTML(
                shipment.origin ?? shipment.source ?? "-"
            );

            const destination = escapeHTML(
                shipment.destination ??
                shipment.destinationAddress ??
                "-"
            );

            const deliveryDate = escapeHTML(
                shipment.expectedDeliveryDate ??
                shipment.deliveryDate ??
                "-"
            );

            return `
                <tr>
                    <td>${id}</td>
                    <td>${origin}</td>
                    <td>${destination}</td>
                    <td>${status}</td>
                    <td>${deliveryDate}</td>
                </tr>
            `;
        }).join("");
    }

    /* ======================================================
       EXCEPTION ANALYTICS TABLE
       ====================================================== */

    function renderExceptionTable() {
        const tableBody =
            document.querySelector("#analyticsExceptionTable tbody") ||
            document.getElementById("analyticsExceptionTableBody");

        if (!tableBody) {
            return;
        }

        if (state.exceptions.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No exception data available.
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = state.exceptions.map(exception => {
            const id = escapeHTML(
                exception.exceptionId ??
                exception.id ??
                "-"
            );

            const shipmentId = escapeHTML(
                exception.shipmentId ?? "-"
            );

            const type = escapeHTML(
                exception.exceptionType ??
                exception.type ??
                "Unknown"
            );

            const priority = escapeHTML(
                exception.priority ?? "Normal"
            );

            const status = escapeHTML(
                exception.status ?? "Unknown"
            );

            return `
                <tr>
                    <td>${id}</td>
                    <td>${shipmentId}</td>
                    <td>${type}</td>
                    <td>${priority}</td>
                    <td>${status}</td>
                </tr>
            `;
        }).join("");
    }

    /* ======================================================
       CHARTS
       ====================================================== */

    function renderCharts(metrics) {
        if (typeof Chart === "undefined") {
            console.warn(
                "Chart.js is not loaded. Skipping analytics charts."
            );
            return;
        }

        renderOrUpdateChart(
            "shipmentAnalyticsChart",
            "shipmentAnalyticsChartInstance",
            {
                type: "bar",
                data: {
                    labels: [
                        "Delivered",
                        "In Transit",
                        "Delayed"
                    ],
                    datasets: [{
                        label: "Shipments",
                        data: [
                            metrics.delivered,
                            metrics.inTransit,
                            metrics.delayed
                        ],
                        backgroundColor: [
                            "#10b981",
                            "#38bdf8",
                            "#f59e0b"
                        ],
                        borderRadius: 6
                    }]
                },
                options: chartOptions()
            }
        );

        renderOrUpdateChart(
            "exceptionAnalyticsChart",
            "exceptionAnalyticsChartInstance",
            {
                type: "doughnut",
                data: {
                    labels: [
                        "Open",
                        "Resolved",
                        "Other"
                    ],
                    datasets: [{
                        data: [
                            metrics.openExceptions,
                            metrics.resolvedExceptions,
                            Math.max(
                                0,
                                metrics.totalExceptions -
                                metrics.openExceptions -
                                metrics.resolvedExceptions
                            )
                        ],
                        backgroundColor: [
                            "#ef4444",
                            "#10b981",
                            "#8b5cf6"
                        ],
                        borderWidth: 2
                    }]
                },
                options: {
                    ...chartOptions(),
                    plugins: {
                        ...chartOptions().plugins,
                        legend: {
                            display: true,
                            position: "bottom"
                        }
                    }
                }
            }
        );
    }

    function chartOptions() {
        return {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: "bottom"
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    }
                }
            }
        };
    }

    function renderOrUpdateChart(canvasId, instanceName, config) {
        const canvas = document.getElementById(canvasId);

        if (!canvas) {
            return;
        }

        if (window[instanceName]) {
            window[instanceName].data = config.data;
            window[instanceName].options = config.options;
            window[instanceName].update();
            return;
        }

        window[instanceName] = new Chart(canvas, config);
    }

    /* ======================================================
       PAGE MESSAGES
       ====================================================== */

    function showMessage(message) {
        let container = document.getElementById("analyticsMessage");

        if (!container) {
            container = document.createElement("div");
            container.id = "analyticsMessage";

            container.style.cssText = `
                margin: 15px 0;
                padding: 12px 16px;
                border-radius: 8px;
                background: #fff1f2;
                border: 1px solid #fecdd3;
                color: #be123c;
                font-size: 14px;
            `;

            const main = document.querySelector(
                "main, .main-content, .dashboard-content, .container"
            );

            if (main) {
                main.prepend(container);
            } else {
                document.body.prepend(container);
            }
        }

        container.textContent = message;
        container.style.display = "block";
    }

    function hideMessage() {
        const container = document.getElementById("analyticsMessage");

        if (container) {
            container.style.display = "none";
        }
    }

    /* ======================================================
       HTML SAFETY
       ====================================================== */

    function escapeHTML(value) {
        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    /* ======================================================
       REFRESH SUPPORT
       ====================================================== */

    window.refreshAnalytics = loadAnalyticsData;

})();