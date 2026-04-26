(function () {
  function track(eventName, target) {
    try {
      fetch("/api/public/track", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          eventName: eventName,
          pagePath: window.location.pathname,
          target: target || ""
        })
      });
    } catch (error) {
      // Tracking must never block the page.
    }
  }

  if (window.location.pathname === "/") {
    track("home_view");
  }
  if (window.location.pathname === "/appointment") {
    track("appointment_form_open");
  }
  if (window.location.pathname.indexOf("/counselors/") === 0) {
    track("counselor_detail_view");
  }

  var toggle = document.querySelector("[data-nav-toggle]");
  var links = document.querySelector("[data-nav-links]");
  if (toggle && links) {
    toggle.addEventListener("click", function () {
      links.classList.toggle("open");
    });
  }

  var banner = document.querySelector("[data-cookie-banner]");
  var close = document.querySelector("[data-cookie-close]");
  var key = "lxPortalCookieClosed";
  if (banner && window.localStorage.getItem(key) !== "true") {
    banner.classList.add("show");
  }
  if (close && banner) {
    close.addEventListener("click", function () {
      window.localStorage.setItem(key, "true");
      banner.classList.remove("show");
    });
  }

  document.querySelectorAll("a[href='/appointment'], a[href$='/appointment']").forEach(function (link) {
    link.addEventListener("click", function () {
      track("appointment_button_click", link.textContent.trim());
    });
  });
})();
