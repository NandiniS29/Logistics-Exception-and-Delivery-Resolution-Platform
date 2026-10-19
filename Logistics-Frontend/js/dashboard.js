const API_BASE_URL = "http://127.0.0.1:8080";

let shipmentData = [];
let exceptionData = [];
let analyticsData = {};
let slaData = {};

let shipmentChartInstance = null;
let exceptionChartInstance = null;


/* =========================================
   LOAD DASHBOARD
========================================= */

async function loadDashboard() {

    try {

        const [
            analyticsResponse,
            shipmentsResponse,
            exceptionsResponse,
            slaResponse
        ] = await Promise.all([

            fetch(
                `${API_BASE_URL}/api/analytics/dashboard`,
                {
                    credentials: "include"
                }
            ),

            fetch(
                `${API_BASE_URL}/api/shipments`,
                {
                    credentials: "include"
                }
            ),

            fetch(
                `${API_BASE_URL}/api/exceptions`,
                {
                    credentials: "include"
                }
            ),

            fetch(
                `${API_BASE_URL}/api/sla/analytics`,
                {
                    credentials: "include"
                }
            )

        ]);


        /* =====================================
           ANALYTICS
        ===================================== */

        if (analyticsResponse.ok) {

            analyticsData =
                await analyticsResponse.json();

        }


        /* =====================================
           SHIPMENTS
        ===================================== */

        if (shipmentsResponse.ok) {

            const data =
                await shipmentsResponse.json();

            shipmentData =
                Array.isArray(data)
                    ? data
                    : Array.isArray(data.content)
                        ? data.content
                        : [];

        }


        /* =====================================
           EXCEPTIONS
        ===================================== */

        if (exceptionsResponse.ok) {

            const data =
                await exceptionsResponse.json();

            exceptionData =
                Array.isArray(data)
                    ? data
                    : Array.isArray(data.content)
                        ? data.content
                        : [];

        }


        /* =====================================
           SLA
        ===================================== */

        if (slaResponse.ok) {

            slaData =
                await slaResponse.json();

        }


        console.log(
            "Dashboard Analytics:",
            analyticsData
        );

        console.log(
            "Dashboard Shipments:",
            shipmentData
        );

        console.log(
            "Dashboard Exceptions:",
            exceptionData
        );

        console.log(
            "Dashboard SLA:",
            slaData
        );


        /* =====================================
           UPDATE DASHBOARD
        ===================================== */

        updateDashboardCards();

        updateShipmentChart();

        updateExceptionChart();

        updateRecentShipments();

        updateRecentExceptions();


    } catch (error) {

        console.error(
            "Dashboard loading error:",
            error
        );

    }

}


/* =========================================
   UPDATE DASHBOARD CARDS
========================================= */

function updateDashboardCards() {

    const totalShipments =
        shipmentData.length;


    let delivered = 0;

    let inTransit = 0;

    let delayed = 0;


    /*
       Determine shipment status.

       IMPORTANT:
       Overdue shipments are counted as
       delayed even if their database status
       is still "In Transit".
    */

    shipmentData.forEach(
        shipment => {

            const status =
                String(
                    shipment.status || ""
                ).toUpperCase();


            const expectedDate =
                shipment.expectedDeliveryDate;


            const isOverdue =
                expectedDate &&
                new Date(expectedDate) <
                new Date();


            if (
                status === "DELIVERED" ||
                status === "DELIVERED SUCCESSFULLY"
            ) {

                delivered++;

            }

            else if (isOverdue) {

                delayed++;

            }

            else if (
                status === "IN TRANSIT" ||
                status === "IN_TRANSIT" ||
                status === "DISPATCHED"
            ) {

                inTransit++;

            }

            else if (
                status === "DELAYED" ||
                status === "OVERDUE"
            ) {

                delayed++;

            }

        }
    );


    /* =====================================
       EXCEPTION COUNTS
    ===================================== */

    let openExceptions = 0;

    let investigating = 0;

    let pendingResolution = 0;

    let resolved = 0;

    let closed = 0;


    exceptionData.forEach(
        exception => {

            const status =
                String(
                    exception.status || ""
                ).toUpperCase();


            if (status === "OPEN") {

                openExceptions++;

            }

            else if (
                status === "INVESTIGATING"
            ) {

                investigating++;

            }

            else if (
                status === "PENDING" ||
                status === "PENDING RESOLUTION"
            ) {

                pendingResolution++;

            }

            else if (status === "RESOLVED") {

                resolved++;

            }

            else if (status === "CLOSED") {

                closed++;

            }

        }
    );


    /* =====================================
       RESOLUTION COUNT
    ===================================== */

    /*
       The Resolution module is separate from
       Exception status.

       Therefore use totalResolutions from
       Analytics API for the dashboard
       "Resolved" card.
    */

    const totalResolutions =
        Number(
            analyticsData.totalResolutions || 0
        );


    /* =====================================
       SLA VIOLATIONS
    ===================================== */

    /*
       SLA API returns:

       totalShipments
       onTimeShipments
       overdueShipments
       slaCompliancePercentage
       totalExceptions
       openExceptions
       closedExceptions

       Therefore overdueShipments is the
       correct value for SLA Violations.
    */

    const slaViolations =
        Number(
            slaData.overdueShipments || 0
        );


    /* =====================================
       COMPENSATION
    ===================================== */

    const compensation =
        Number(
            analyticsData.totalCompensation || 0
        );


    /* =====================================
       UPDATE HTML
    ===================================== */

    setText(
        "totalShipments",
        totalShipments
    );


    setText(
        "deliveredShipments",
        delivered
    );


    setText(
        "inTransitShipments",
        inTransit
    );


    setText(
        "delayedShipments",
        delayed
    );


    setText(
        "openExceptions",
        openExceptions
    );


    setText(
        "resolvedExceptions",
        totalResolutions
    );


    setText(
        "slaViolations",
        slaViolations
    );


    setText(
        "compensation",
        "₹" +
        compensation.toLocaleString("en-IN")
    );


    console.log(
        "Final Dashboard Values:",
        {
            totalShipments,
            delivered,
            inTransit,
            delayed,
            openExceptions,
            investigating,
            pendingResolution,
            resolved,
            closed,
            totalResolutions,
            slaViolations,
            compensation
        }
    );

}


/* =========================================
   SHIPMENT CHART
========================================= */

function updateShipmentChart() {

    const canvas =
        document.getElementById(
            "shipmentChart"
        );


    if (!canvas) {

        return;

    }


    let delivered = 0;

    let inTransit = 0;

    let delayed = 0;


    shipmentData.forEach(
        shipment => {

            const status =
                String(
                    shipment.status || ""
                ).toUpperCase();


            const expectedDate =
                shipment.expectedDeliveryDate;


            const isOverdue =
                expectedDate &&
                new Date(expectedDate) <
                new Date();


            if (
                status === "DELIVERED" ||
                status === "DELIVERED SUCCESSFULLY"
            ) {

                delivered++;

            }

            else if (isOverdue) {

                delayed++;

            }

            else if (
                status === "IN TRANSIT" ||
                status === "IN_TRANSIT" ||
                status === "DISPATCHED"
            ) {

                inTransit++;

            }

            else if (
                status === "DELAYED" ||
                status === "OVERDUE"
            ) {

                delayed++;

            }

        }
    );


    if (shipmentChartInstance) {

        shipmentChartInstance.destroy();

    }


    shipmentChartInstance =
        new Chart(
            canvas.getContext("2d"),
            {

                type: "bar",

                data: {

                    labels: [
                        "Delivered",
                        "In Transit",
                        "Delayed"
                    ],

                    datasets: [

                        {
                            label: "Shipments",

                            data: [
                                delivered,
                                inTransit,
                                delayed
                            ]
                        }

                    ]

                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        legend: {
                            display: true
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

                }

            }
        );

}


/* =========================================
   EXCEPTION CHART
========================================= */

function updateExceptionChart() {

    const canvas =
        document.getElementById(
            "exceptionChart"
        );


    if (!canvas) {

        return;

    }


    const counts = {};


    exceptionData.forEach(
        exception => {

            const type =
                exception.exceptionType ||
                "Other";


            counts[type] =
                (counts[type] || 0) + 1;

        }
    );


    const labels =
        Object.keys(counts);


    const values =
        Object.values(counts);


    if (exceptionChartInstance) {

        exceptionChartInstance.destroy();

    }


    exceptionChartInstance =
        new Chart(
            canvas.getContext("2d"),
            {

                type: "doughnut",

                data: {

                    labels:
                        labels.length
                            ? labels
                            : ["No Exceptions"],

                    datasets: [

                        {

                            label: "Exceptions",

                            data:
                                values.length
                                    ? values
                                    : [1]

                        }

                    ]

                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        legend: {

                            position: "bottom"

                        }

                    }

                }

            }
        );

}


/* =========================================
   RECENT SHIPMENTS
========================================= */

function updateRecentShipments() {

    const table =
        document.getElementById(
            "recentShipmentsTable"
        );


    if (!table) {

        return;

    }


    const tbody =
        table.querySelector("tbody");


    if (!tbody) {

        return;

    }


    const rows =
        shipmentData
            .slice()
            .sort(
                (a, b) =>
                    Number(
                        b.shipmentId || 0
                    ) -
                    Number(
                        a.shipmentId || 0
                    )
            )
            .slice(0, 5);


    if (rows.length === 0) {

        tbody.innerHTML = `

            <tr>

                <td colspan="5">

                    No shipment records found.

                </td>

            </tr>

        `;

        return;

    }


    tbody.innerHTML =
        rows.map(
            shipment => {

                const status =
                    getDisplayShipmentStatus(
                        shipment
                    );


                const statusClass =
                    getShipmentStatusClass(
                        status
                    );


                return `

                    <tr>

                        <td>

                            ${escapeHtml(
                                shipment.trackingNumber ||
                                "-"
                            )}

                        </td>


                        <td>

                            ${escapeHtml(
                                shipment.destination ||
                                "-"
                            )}

                        </td>


                        <td>

                            ${formatDate(
                                shipment.expectedDeliveryDate
                            )}

                        </td>


                        <td>

                            <span class="status-badge ${statusClass}">

                                ${escapeHtml(
                                    status
                                )}

                            </span>

                        </td>


                        <td>

                            <a
                                href="tracking.html?shipmentId=${encodeURIComponent(
                                    shipment.shipmentId
                                )}"
                                class="table-action"
                            >

                                Track

                            </a>

                        </td>

                    </tr>

                `;

            }
        ).join("");

}


/* =========================================
   DISPLAY SHIPMENT STATUS
========================================= */

function getDisplayShipmentStatus(shipment) {

    const status =
        String(
            shipment.status || ""
        ).toUpperCase();


    const expectedDate =
        shipment.expectedDeliveryDate;


    const isOverdue =
        expectedDate &&
        new Date(expectedDate) <
        new Date();


    if (
        status === "DELIVERED" ||
        status === "DELIVERED SUCCESSFULLY"
    ) {

        return "Delivered";

    }


    if (isOverdue) {

        return "Delayed";

    }


    if (
        status === "IN TRANSIT" ||
        status === "IN_TRANSIT"
    ) {

        return "In Transit";

    }


    if (status === "DISPATCHED") {

        return "In Transit";

    }


    if (
        status === "DELAYED" ||
        status === "OVERDUE"
    ) {

        return "Delayed";

    }


    return shipment.status || "-";

}


/* =========================================
   SHIPMENT STATUS CLASS
========================================= */

function getShipmentStatusClass(status) {

    switch (
        String(status).toUpperCase()
    ) {

        case "DELIVERED":

            return "delivered";


        case "IN TRANSIT":

            return "transit";


        case "DELAYED":

            return "delayed";


        default:

            return "";

    }

}


/* =========================================
   RECENT EXCEPTIONS
========================================= */

function updateRecentExceptions() {

    /*
       dashboard.html does not currently have
       an ID for the Recent Exceptions table.

       Therefore locate it using its headers.
    */

    const tables =
        document.querySelectorAll(
            "table"
        );


    let table = null;


    tables.forEach(
        candidate => {

            if (table) {

                return;

            }


            const headers =
                Array.from(
                    candidate.querySelectorAll(
                        "thead th"
                    )
                )
                .map(
                    th =>
                        th.textContent
                            .trim()
                            .toLowerCase()
                );


            const isExceptionTable =
                headers.includes("exception") &&
                headers.includes("shipment") &&
                headers.includes("type") &&
                headers.includes("priority") &&
                headers.includes("status");


            if (isExceptionTable) {

                table = candidate;

            }

        }
    );


    if (!table) {

        console.warn(
            "Recent Exceptions table was not found."
        );

        return;

    }


    const tbody =
        table.querySelector("tbody");


    if (!tbody) {

        console.warn(
            "Recent Exceptions tbody was not found."
        );

        return;

    }


    const rows =
        exceptionData
            .slice()
            .sort(
                (a, b) =>
                    Number(
                        b.exceptionId || 0
                    ) -
                    Number(
                        a.exceptionId || 0
                    )
            )
            .slice(0, 5);


    if (rows.length === 0) {

        tbody.innerHTML = `

            <tr>

                <td colspan="5">

                    No exception records found.

                </td>

            </tr>

        `;

        return;

    }


    tbody.innerHTML =
        rows.map(
            exception => {

                const exceptionId =
                    exception.exceptionId ?? "-";


                const shipmentId =
                    exception.shipmentId ?? "-";


                const type =
                    exception.exceptionType || "-";


                const priority =
                    String(
                        exception.priority || "-"
                    ).toUpperCase();


                const status =
                    String(
                        exception.status || "-"
                    ).toUpperCase();


                return `

                    <tr>

                        <td>

                            ${escapeHtml(
                                exceptionId
                            )}

                        </td>


                        <td>

                            ${escapeHtml(
                                shipmentId
                            )}

                        </td>


                        <td>

                            ${escapeHtml(
                                type
                            )}

                        </td>


                        <td>

                            <span class="priority ${getPriorityClass(
                                priority
                            )}">

                                ${escapeHtml(
                                    priority
                                )}

                            </span>

                        </td>


                        <td>

                            <span class="status-badge ${getStatusClass(
                                status
                            )}">

                                ${escapeHtml(
                                    status
                                )}

                            </span>

                        </td>

                    </tr>

                `;

            }
        ).join("");

}


/* =========================================
   PRIORITY CLASS
========================================= */

function getPriorityClass(priority) {

    switch (
        String(priority).toUpperCase()
    ) {

        case "HIGH":

            return "high";


        case "MEDIUM":

            return "medium";


        case "LOW":

            return "low";


        default:

            return "";

    }

}


/* =========================================
   STATUS CLASS
========================================= */

function getStatusClass(status) {

    switch (
        String(status).toUpperCase()
    ) {

        case "OPEN":

            return "open";


        case "INVESTIGATING":

            return "investigating";


        case "PENDING":

        case "PENDING RESOLUTION":

            return "pending";


        case "RESOLVED":

            return "resolved";


        case "CLOSED":

            return "closed";


        default:

            return "";

    }

}


/* =========================================
   DATE FORMAT
========================================= */

function formatDate(value) {

    if (!value) {

        return "-";

    }


    const date =
        new Date(value);


    if (
        isNaN(
            date.getTime()
        )
    ) {

        return escapeHtml(
            String(value)
        );

    }


    return date.toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );

}


/* =========================================
   SET TEXT
========================================= */

function setText(
    elementId,
    value
) {

    const element =
        document.getElementById(
            elementId
        );


    if (element) {

        element.textContent =
            value;

    }

}


/* =========================================
   HTML ESCAPE
========================================= */

function escapeHtml(value) {

    return String(value)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}


/* =========================================
   SEARCH
========================================= */

function setupSearch() {

    const searchInput =
        document.getElementById(
            "dashboardSearch"
        );


    if (!searchInput) {

        return;

    }


    searchInput.addEventListener(
        "input",
        function () {

            const value =
                this.value
                    .trim()
                    .toLowerCase();


            const rows =
                document.querySelectorAll(
                    "table tbody tr"
                );


            rows.forEach(
                row => {

                    const text =
                        row.textContent
                            .toLowerCase();


                    row.style.display =
                        !value ||
                        text.includes(value)
                            ? ""
                            : "none";

                }
            );

        }
    );

}


/* =========================================
   PERFORMANCE FILTER
========================================= */

function setupPerformanceFilter() {

    const filter =
        document.getElementById(
            "performanceFilter"
        );


    if (!filter) {

        return;

    }


    filter.addEventListener(
        "change",
        function () {

            console.log(
                "Performance filter:",
                this.value
            );

        }
    );

}


/* =========================================
   INITIALIZE
========================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadDashboard();

        setupSearch();

        setupPerformanceFilter();

    }
);