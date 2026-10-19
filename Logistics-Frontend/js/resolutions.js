/* =====================================================
   LOGISTICS PLATFORM - RESOLUTION MANAGEMENT
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadResolutions();

    const form = document.getElementById("resolutionForm");

    if (form) {
        form.addEventListener("submit", saveResolution);
    }

    const refreshButton = document.getElementById("refreshResolutions");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadResolutions);
    }
});

async function loadResolutions() {
    const tableBody =
        document.querySelector("#resolutionsTable tbody") ||
        document.getElementById("resolutionsTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="8">Loading resolutions...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/resolutions`);

        if (!response.ok) {
            throw new Error("Unable to load resolutions.");
        }

        const resolutions = await response.json();

        if (!Array.isArray(resolutions) || resolutions.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="8">No resolutions found.</td></tr>';
            return;
        }

        tableBody.innerHTML = resolutions.map(item => `
            <tr>
                <td>${item.resolutionId ?? item.id ?? "-"}</td>
                <td>${item.exceptionId ?? "-"}</td>
                <td>${item.resolutionType ?? item.type ?? "-"}</td>
                <td>${item.description ?? "-"}</td>
                <td>${item.compensationAmount ?? item.amount ?? "-"}</td>
                <td>${item.status ?? "-"}</td>
                <td>${item.resolutionDate ?? "-"}</td>
                <td>
                    <button type="button"
                        onclick="viewResolution(${Number(item.resolutionId ?? item.id)})">
                        View
                    </button>
                </td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Resolution loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="8">Could not load resolutions. Verify the backend endpoint.</td></tr>';
    }
}

async function saveResolution(event) {
    event.preventDefault();

    const form = event.target;
    const resolutionData = Object.fromEntries(new FormData(form).entries());

    try {
        const response = await fetch(`${API_BASE_URL}/resolutions`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(resolutionData)
        });

        if (!response.ok) {
            throw new Error("Unable to save resolution.");
        }

        alert("Resolution saved successfully.");
        form.reset();
        loadResolutions();

    } catch (error) {
        console.error("Resolution saving error:", error);
        alert("Could not save the resolution. Verify the backend API.");
    }
}

async function viewResolution(id) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/resolutions/${encodeURIComponent(id)}`
        );

        if (!response.ok) {
            throw new Error("Resolution details could not be loaded.");
        }

        const data = await response.json();

        alert(JSON.stringify(data, null, 2));

    } catch (error) {
        console.error(error);
        alert("Unable to view resolution details.");
    }
}