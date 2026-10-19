/* =====================================================
   LOGISTICS PLATFORM - PACKAGE MANAGEMENT
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadPackages();

    const form = document.getElementById("packageForm");

    if (form) {
        form.addEventListener("submit", savePackage);
    }

    const refreshButton = document.getElementById("refreshPackages");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadPackages);
    }
});

async function loadPackages() {
    const tableBody =
        document.querySelector("#packagesTable tbody") ||
        document.getElementById("packagesTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="8">Loading packages...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/packages`);

        if (!response.ok) {
            throw new Error("Unable to load packages.");
        }

        const packages = await response.json();

        if (!Array.isArray(packages) || packages.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="8">No packages found.</td></tr>';
            return;
        }

        tableBody.innerHTML = packages.map(pkg => `
            <tr>
                <td>${pkg.packageId ?? pkg.id ?? "-"}</td>
                <td>${pkg.shipmentId ?? "-"}</td>
                <td>${pkg.packageName ?? pkg.name ?? "-"}</td>
                <td>${pkg.description ?? "-"}</td>
                <td>${pkg.weight ?? "-"}</td>
                <td>${pkg.quantity ?? "-"}</td>
                <td>${pkg.status ?? "-"}</td>
                <td>
                    <button type="button"
                        onclick="viewPackage(${Number(pkg.packageId ?? pkg.id)})">
                        View
                    </button>
                </td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Package loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="8">Could not load packages. Verify the backend endpoint.</td></tr>';
    }
}

async function savePackage(event) {
    event.preventDefault();

    const form = event.target;
    const packageData = Object.fromEntries(new FormData(form).entries());

    try {
        const response = await fetch(`${API_BASE_URL}/packages`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(packageData)
        });

        if (!response.ok) {
            throw new Error("Unable to save package.");
        }

        alert("Package saved successfully.");
        form.reset();
        loadPackages();

    } catch (error) {
        console.error("Package saving error:", error);
        alert("Could not save the package. Verify the API and required fields.");
    }
}

async function viewPackage(id) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/packages/${encodeURIComponent(id)}`
        );

        if (!response.ok) {
            throw new Error("Package details could not be loaded.");
        }

        const data = await response.json();

        alert(JSON.stringify(data, null, 2));

    } catch (error) {
        console.error(error);
        alert("Unable to view package details.");
    }
}