(function () {
  var button = document.querySelector("[data-admin-menu]");
  var sidebar = document.querySelector(".admin-sidebar");
  if (button && sidebar) {
    button.addEventListener("click", function () {
      sidebar.classList.toggle("open");
    });
  }
})();

(function () {
  if (!window.ChatAdminPage) {
    return;
  }
  var body = document.querySelector("[data-admin-table-body]");
  var detail = document.querySelector("[data-admin-detail]");
  var messages = document.querySelector("[data-admin-chat-messages]");
  var refresh = document.querySelector("[data-refresh]");
  if (!body) {
    return;
  }

  function text(value) {
    return value === null || value === undefined || value === "" ? "-" : String(value);
  }

  function request(url) {
    return fetch(url, { credentials: "same-origin", headers: { Accept: "application/json" } })
      .then(function (response) {
        return response.json().then(function (payload) {
          if (!response.ok || payload.code !== 0) {
            throw new Error(payload.message || "请求失败");
          }
          return payload.data;
        });
      });
  }

  function renderRows(items) {
    if (!items.length) {
      body.innerHTML = '<tr><td colspan="8" class="empty-cell">暂无聊天会话</td></tr>';
      return;
    }
    body.innerHTML = items.map(function (item) {
      return "<tr data-chat-id=\"" + item.id + "\">"
        + "<td>" + item.id + "</td>"
        + "<td>" + text(item.visitorName) + "</td>"
        + "<td>" + text(item.visitorContact) + "</td>"
        + "<td>" + text(item.topic) + "</td>"
        + "<td>" + text(item.status) + "</td>"
        + "<td>" + (item.crisisFlagged ? "是" : "否") + "</td>"
        + "<td>" + (item.aiEnabled ? "已启用" : "预留") + "</td>"
        + "<td>" + text(item.updatedAt).replace("T", " ").slice(0, 16) + "</td>"
        + "</tr>";
    }).join("");
  }

  function load() {
    body.innerHTML = '<tr><td colspan="8" class="empty-cell">正在加载...</td></tr>';
    request("/api/admin/chat/sessions").then(function (data) {
      renderRows(data.items || []);
    }).catch(function (error) {
      body.innerHTML = '<tr><td colspan="8" class="empty-cell">' + error.message + "</td></tr>";
    });
  }

  body.addEventListener("click", function (event) {
    var row = event.target.closest("[data-chat-id]");
    if (!row || !detail || !messages) {
      return;
    }
    request("/api/admin/chat/sessions/" + row.getAttribute("data-chat-id")).then(function (session) {
      detail.hidden = false;
      messages.innerHTML = (session.messages || []).map(function (message) {
        return '<div class="admin-chat-message ' + String(message.role).toLowerCase() + '">'
          + '<strong>' + message.role + '</strong><p>' + text(message.content) + '</p></div>';
      }).join("");
    });
  });

  if (refresh) {
    refresh.addEventListener("click", load);
  }
  load();
})();

(function () {
  var config = window.AdminPageConfig;
  var root = document.querySelector("[data-admin-resource]");
  if (!config || !root) {
    return;
  }

  var state = { items: [], editing: null };
  var head = root.querySelector("[data-admin-table-head]");
  var body = root.querySelector("[data-admin-table-body]");
  var message = root.querySelector("[data-admin-message]");
  var editor = document.querySelector("[data-admin-editor]");
  var form = document.querySelector("[data-admin-form]");
  var formTitle = document.querySelector("[data-admin-form-title]");

  function text(value) {
    if (value === null || value === undefined || value === "") {
      return "-";
    }
    return String(value);
  }

  function escapeHtml(value) {
    return text(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function getCsrfHeaders() {
    var token = document.querySelector("meta[name='_csrf']");
    var header = document.querySelector("meta[name='_csrf_header']");
    if (!token || !header) {
      return {};
    }
    var headers = {};
    headers[header.getAttribute("content")] = token.getAttribute("content");
    return headers;
  }

  function formatDateTime(value) {
    if (!value) {
      return "-";
    }
    return String(value).replace("T", " ").slice(0, 16);
  }

  function statusLabel(value) {
    var labels = {
      PENDING_CONTACT: "待联系",
      CONTACTED: "已联系",
      SCHEDULED: "已预约",
      COMPLETED: "已完成",
      CANCELED: "已取消",
      DRAFT: "草稿",
      PUBLISHED: "已发布",
      ARCHIVED: "已归档"
    };
    return labels[value] || text(value);
  }

  function statusClass(value) {
    return String(value || "draft").toLowerCase().replace(/_/g, "-");
  }

  function setMessage(content, type) {
    if (!message) {
      return;
    }
    message.textContent = content || "";
    message.className = "admin-alert " + (type || "info");
    message.hidden = !content;
  }

  function normalizeItems(payload) {
    var data = payload && payload.data;
    if (Array.isArray(data)) {
      return data;
    }
    if (data && Array.isArray(data.items)) {
      return data.items;
    }
    return [];
  }

  function renderHead() {
    var columns = config.columns.concat([{ key: "__actions", label: "操作" }]);
    head.innerHTML = "<tr>" + columns.map(function (column) {
      return "<th>" + escapeHtml(column.label) + "</th>";
    }).join("") + "</tr>";
  }

  function renderCell(item, column) {
    var value = item[column.key];
    if (column.type === "datetime") {
      return formatDateTime(value);
    }
    if (column.type === "status") {
      return '<span class="status ' + statusClass(value) + '">' + escapeHtml(statusLabel(value)) + "</span>";
    }
    return escapeHtml(value);
  }

  function renderRows() {
    if (!state.items.length) {
      body.innerHTML = '<tr><td colspan="' + (config.columns.length + 1) + '" class="empty-cell">暂无数据</td></tr>';
      return;
    }
    body.innerHTML = state.items.map(function (item) {
      var cells = config.columns.map(function (column) {
        return "<td>" + renderCell(item, column) + "</td>";
      }).join("");
      return "<tr>" + cells + '<td><button class="table-action" type="button" data-admin-edit="' + item[config.rowId] + '">编辑</button></td></tr>';
    }).join("");
  }

  function fieldValue(item, field) {
    var value = item && item[field.key];
    if (field.type === "datetime-local" && value) {
      return String(value).slice(0, 16);
    }
    if (value === undefined || value === null) {
      return field.type === "number" ? 0 : "";
    }
    return value;
  }

  function renderInput(field, item) {
    var value = fieldValue(item, field);
    var required = field.required ? " required" : "";
    var readonly = field.type === "readonly" ? " readonly" : "";
    var cls = "form-field" + (field.span === 2 ? " wide" : "") + (field.large ? " large" : "");
    var html = '<label class="' + cls + '"><span>' + escapeHtml(field.label) + "</span>";
    if (field.type === "textarea" || field.large) {
      html += '<textarea name="' + field.key + '"' + required + readonly + ">" + escapeHtml(value).replace(/^-$/, "") + "</textarea>";
    } else if (field.type === "select") {
      html += '<select name="' + field.key + '"' + required + ">";
      (field.options || []).forEach(function (option) {
        html += '<option value="' + escapeHtml(option) + '"' + (option === value ? " selected" : "") + ">" + escapeHtml(statusLabel(option)) + "</option>";
      });
      html += "</select>";
    } else {
      var type = field.type === "readonly" ? "text" : (field.type || "text");
      html += '<input type="' + escapeHtml(type) + '" name="' + field.key + '" value="' + escapeHtml(value).replace(/^-$/, "") + '"' + required + readonly + ">";
    }
    return html + "</label>";
  }

  function openEditor(item) {
    state.editing = item || null;
    if (!editor || !form) {
      return;
    }
    if (formTitle) {
      formTitle.textContent = item ? config.formTitle : (config.createLabel || config.formTitle);
    }
    form.innerHTML = config.formFields.map(function (field) {
      return renderInput(field, item);
    }).join("") + '<div class="form-actions"><button class="ghost-button" type="button" data-admin-cancel>取消</button><button class="primary-button" type="submit">' + (config.submitText || "保存") + "</button></div>";
    editor.hidden = false;
    editor.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  function closeEditor() {
    state.editing = null;
    if (editor) {
      editor.hidden = true;
    }
  }

  function payloadFromForm() {
    var formData = new FormData(form);
    var payload = {};
    config.formFields.forEach(function (field) {
      if (field.type === "readonly") {
        return;
      }
      var value = formData.get(field.key);
      if (field.type === "number") {
        payload[field.key] = Number(value || 0);
      } else if (field.type === "datetime-local") {
        payload[field.key] = value ? String(value).replace("T", "T") + ":00" : null;
      } else {
        payload[field.key] = value;
      }
    });
    return payload;
  }

  function request(url, options) {
    var headers = Object.assign({ "Accept": "application/json" }, getCsrfHeaders(), options && options.headers);
    return fetch(url, Object.assign({ credentials: "same-origin" }, options, { headers: headers }))
      .then(function (response) {
        return response.json().then(function (payload) {
          if (!response.ok || payload.code !== 0) {
            throw new Error(payload.message || "请求失败");
          }
          return payload;
        });
      });
  }

  function load() {
    setMessage("", "info");
    body.innerHTML = '<tr><td colspan="' + (config.columns.length + 1) + '" class="empty-cell">正在加载...</td></tr>';
    request(config.endpoint, { method: "GET" })
      .then(function (payload) {
        state.items = normalizeItems(payload);
        renderRows();
      })
      .catch(function (error) {
        state.items = [];
        renderRows();
        setMessage(error.message, "error");
      });
  }

  function saveAppointment() {
    var id = state.editing && state.editing[config.rowId];
    var payload = payloadFromForm();
    return request(config.endpoint + "/" + id + "/status", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status: payload.status })
    }).then(function () {
      return request(config.endpoint + "/" + id + "/note", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ note: payload.internalNote || "" })
      });
    });
  }

  function saveGeneric() {
    var item = state.editing;
    var url = item ? config.endpoint + "/" + item[config.rowId] : config.endpoint;
    return request(url, {
      method: item ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payloadFromForm())
    });
  }

  renderHead();
  load();

  root.addEventListener("click", function (event) {
    var editButton = event.target.closest("[data-admin-edit]");
    if (editButton) {
      var id = editButton.getAttribute("data-admin-edit");
      var item = state.items.find(function (entry) {
        return String(entry[config.rowId]) === String(id);
      });
      openEditor(item);
    }
  });

  document.addEventListener("click", function (event) {
    if (event.target.closest("[data-admin-refresh]")) {
      load();
    }
    if (event.target.closest("[data-admin-create]")) {
      openEditor(null);
    }
    if (event.target.closest("[data-admin-cancel]")) {
      closeEditor();
    }
  });

  if (form) {
    form.addEventListener("submit", function (event) {
      event.preventDefault();
      var saver = config.resource === "appointments" ? saveAppointment : saveGeneric;
      saver()
        .then(function () {
          closeEditor();
          setMessage("保存成功", "success");
          load();
        })
        .catch(function (error) {
          setMessage(error.message, "error");
        });
    });
  }
})();
