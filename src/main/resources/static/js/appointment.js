(function () {
  var form = document.querySelector("[data-appointment-form]");
  var message = document.querySelector("[data-form-message]");
  if (!form || !message) {
    return;
  }

  form.addEventListener("submit", function (event) {
    event.preventDefault();
    if (!form.checkValidity()) {
      message.textContent = "请先补全必填信息，并确认隐私政策与服务边界。";
      form.reportValidity();
      return;
    }

    var data = new FormData(form);
    var payload = {};
    data.forEach(function (value, key) {
      if (key === "privacyAgreed" || key === "emergencyAcknowledged" || key === "acceptsRecommendation") {
        payload[key] = value === "on" || value === "true";
      } else if (key === "preferredCounselorId") {
        payload[key] = value ? Number(value) : null;
      } else {
        payload[key] = value;
      }
    });

    message.textContent = "正在提交预约...";
    fetch("/api/public/appointments", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    }).then(function (response) {
      if (!response.ok) {
        throw new Error("提交失败");
      }
      return response.json();
    }).then(function () {
      try {
        fetch("/api/public/track", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            eventName: "appointment_form_submit",
            pagePath: window.location.pathname,
            target: "appointment"
          })
        });
      } catch (error) {
        // Ignore tracking failures.
      }
      window.location.href = "/appointment-success";
    }).catch(function () {
      message.textContent = "提交失败，请稍后再试，或通过页面底部联系电话预约。";
    });
  });
})();
