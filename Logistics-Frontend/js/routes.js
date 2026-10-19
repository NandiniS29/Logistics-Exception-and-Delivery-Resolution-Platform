/* =====================================================
   LOGISTICS PLATFORM - ROUTE MANAGEMENT
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadRoutes();

    const form = document.getElementById("routeForm");

    if (form) {
        form.addEventListener("submit", saveRoute);
    }

    const refreshButton = document.getElementById("refreshRoutes");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadRoutes);
    }
});

async function loadRoutes() {
    const tableBody =
        document.querySelector("#routesTable tbody") ||
        document.getElementById("routesTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="7">Loading routes...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/routes`);

        if (!response.ok) {
            throw new Error("Unable to load routes.");
        }

        const routes = await response.json();

        if (!Array.isArray(routes) || routes.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="7">No routes found.</td></tr>';
            return;
        }

        tableBody.innerHTML = routes.map(route => `
            <tr>
                <td>${route.routeId ?? route.id ?? "-"}</td>
                <td>${route.routeName ?? route.name ?? "-"}</td>
                <td>${route.origin ?? route.source ?? "-"}</td>
                <td>${route.destination ?? route.destinationCity ?? "-"}</td>
                <td>${route.distance ?? "-"}</td>
                <td>${route.status ?? "-"}</td>
                <td>
                    <button type="button"
                        onclick="viewRoute(${Number(route.routeId ?? route.id)})">
                        View
                    </button>
                </td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Route loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="7">Could not load routes. Verify the backend endpoint.</td></tr>';
    }
}

async function saveRoute(event) {
    event.preventDefault();

    const form = event.target;
    const routeData = Object.fromEntries(new FormData(form).entries());

    try {
        const response = await fetch(`${API_BASE_URL}/routes`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(routeData)
        });

        if (!response.ok) {
            throw new Error("Unable to save route.");
        }

        alert("Route saved successfully.");
        form.reset();
        loadRoutes();

    } catch (error) {
        console.error("Route saving error:", error);
        alert("Could not save the route. Verify the backend API.");
    }
}

async function viewRoute(id) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/routes/${encodeURIComponent(id)}`
        );

        if (!response.ok) {
            throw new Error("Route details could not be loaded.");
        }

        const data = await response.json();

        alert(JSON.stringify(data, null, 2));

    } catch (error) {
        console.error(error);
        alert("Unable to view route details.");
    }
}