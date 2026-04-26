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

  var chatToggle = document.querySelector("[data-chat-toggle]");
  var chatClose = document.querySelector("[data-chat-close]");
  var chatPanel = document.querySelector("[data-chat-panel]");
  var chatMessages = document.querySelector("[data-chat-messages]");
  var chatForm = document.querySelector("[data-chat-form]");
  var chatInput = document.querySelector("[data-chat-input]");
  var chatName = document.querySelector("[data-chat-name]");
  var chatContact = document.querySelector("[data-chat-contact]");
  var chatTopic = document.querySelector("[data-chat-topic]");
  var chatSessionId = window.localStorage.getItem("lxPortalChatSessionId");

  function appendMessage(role, content, crisis) {
    if (!chatMessages) {
      return;
    }
    var item = document.createElement("div");
    item.className = "chat-message " + (role === "VISITOR" ? "visitor" : "assistant");
    if (crisis) {
      item.className += " crisis";
    }
    item.textContent = content;
    chatMessages.appendChild(item);
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  function renderSession(session) {
    if (!chatMessages || !session || !session.messages) {
      return;
    }
    chatMessages.innerHTML = "";
    session.messages.forEach(function (message) {
      appendMessage(message.role, message.content, message.crisisFlagged);
    });
  }

  function startChat() {
    return fetch("/api/public/chat/sessions", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        visitorName: chatName ? chatName.value : "",
        visitorContact: chatContact ? chatContact.value : "",
        topic: chatTopic ? chatTopic.value : ""
      })
    }).then(function (response) {
      return response.json();
    }).then(function (body) {
      chatSessionId = body.data.id;
      window.localStorage.setItem("lxPortalChatSessionId", chatSessionId);
      renderSession(body.data);
    });
  }

  function ensureChat() {
    if (chatSessionId) {
      return fetch("/api/public/chat/sessions/" + chatSessionId)
        .then(function (response) {
          if (!response.ok) {
            throw new Error("session lost");
          }
          return response.json();
        }).then(function (body) {
          renderSession(body.data);
        }).catch(function () {
          window.localStorage.removeItem("lxPortalChatSessionId");
          chatSessionId = null;
          return startChat();
        });
    }
    return startChat();
  }

  if (chatToggle && chatPanel) {
    chatToggle.addEventListener("click", function () {
      chatPanel.classList.add("open");
      ensureChat();
      track("chat_open");
    });
  }
  if (chatClose && chatPanel) {
    chatClose.addEventListener("click", function () {
      chatPanel.classList.remove("open");
    });
  }
  if (chatForm && chatInput) {
    chatForm.addEventListener("submit", function (event) {
      event.preventDefault();
      var content = chatInput.value.trim();
      if (!content) {
        return;
      }
      var send = function () {
        appendMessage("VISITOR", content, false);
        chatInput.value = "";
        return fetch("/api/public/chat/sessions/" + chatSessionId + "/messages", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            content: content,
            visitorName: chatName ? chatName.value : "",
            visitorContact: chatContact ? chatContact.value : "",
            topic: chatTopic ? chatTopic.value : ""
          })
        }).then(function (response) {
          return response.json();
        }).then(function (body) {
          renderSession(body.data);
          track("chat_message_send", chatTopic ? chatTopic.value : "");
        }).catch(function () {
          appendMessage("ASSISTANT", "消息暂时没有发送成功，请稍后再试，或直接提交预约表单。", false);
        });
      };
      (chatSessionId ? Promise.resolve() : startChat()).then(send);
    });
  }
})();
