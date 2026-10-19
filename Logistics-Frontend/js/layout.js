/* =====================================================
   LOGISTICS PLATFORM
   Shared Sidebar Navigation and Theme Management
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    setActiveNavigation();
    initializeTheme();
});

/* -----------------------------------------------------
   1. Highlight only the currently opened page
   ----------------------------------------------------- */
function setActiveNavigation() {
    const currentPage = window.location.pathname
        .split("/")
        .pop()
        .toLowerCase();

    const links = document.querySelectorAll(
        ".sidebar .nav-item, .sidebar .nav-link, .sidebar-nav a"
    );

    links.forEach(function (link) {
        const href = link.getAttribute("href");

        link.classList.remove("active");

        if (!href || href === "#") {
            return;
        }

        const linkPage = href
            .split("/")
            .pop()
            .split("?")[0]
            .toLowerCase();

        if (linkPage === currentPage) {
            link.classList.add("active");
        }
    });
}

/* -----------------------------------------------------
   2. Initialize saved theme
   ----------------------------------------------------- */
function initializeTheme() {
    const savedTheme =
        localStorage.getItem("logisticsTheme") || "light";

    applyTheme(savedTheme);
    addThemeToggle();
}

/* -----------------------------------------------------
   3. Apply Light Mode or Dark Mode
   ----------------------------------------------------- */
function applyTheme(theme) {
    const selectedTheme = theme === "dark" ? "dark" : "light";

    document.documentElement.setAttribute(
        "data-theme",
        selectedTheme
    );

    localStorage.setItem("logisticsTheme", selectedTheme);

    const toggle = document.getElementById("themeToggle");

    if (toggle) {
        toggle.checked = selectedTheme === "dark";
    }

    const label = document.getElementById("themeToggleLabel");

    if (label) {
        label.textContent =
            selectedTheme === "dark" ? "Dark Mode" : "Light Mode";
    }
}

/* -----------------------------------------------------
   4. Add a theme toggle to the sidebar
   ----------------------------------------------------- */
function addThemeToggle() {
    if (document.getElementById("themeToggle")) {
        return;
    }

    const sidebar = document.querySelector(".sidebar");

    if (!sidebar) {
        return;
    }

    const container = document.createElement("div");

    container.className = "theme-toggle-container";

    container.innerHTML = `
        <div class="theme-toggle-row">
            <span class="theme-icon" aria-hidden="true">🌙</span>

            <span
                id="themeToggleLabel"
                class="theme-toggle-text"
            >
                Light Mode
            </span>

            <label class="theme-switch">
                <input
                    type="checkbox"
                    id="themeToggle"
                    aria-label="Switch between Light Mode and Dark Mode"
                >

                <span class="theme-slider"></span>
            </label>
        </div>
    `;

    const sidebarBottom = sidebar.querySelector(".sidebar-bottom");

    if (sidebarBottom) {
        sidebar.insertBefore(container, sidebarBottom);
    } else {
        sidebar.appendChild(container);
    }

    const toggle = document.getElementById("themeToggle");

    toggle.checked =
        document.documentElement.getAttribute("data-theme") === "dark";

    toggle.addEventListener("change", function () {
        applyTheme(this.checked ? "dark" : "light");
    });

    applyTheme(
        localStorage.getItem("logisticsTheme") || "light"
    );
}
