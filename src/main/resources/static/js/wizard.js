/*
 * Progressive enhancement for the "Allgemeine Angaben" step:
 * show only as many birth-date fields as persons selected. Without JavaScript
 * all fields stay visible and the server-side validation remains authoritative.
 */
(function () {
	"use strict";

	function syncPersonFields() {
		var select = document.querySelector("[data-person-count]");
		if (!select) {
			return;
		}
		var count = parseInt(select.value, 10) || 1;
		document.querySelectorAll(".person-field").forEach(function (field) {
			var index = parseInt(field.getAttribute("data-person"), 10);
			var visible = index <= count;
			field.hidden = !visible;
			field.querySelectorAll("input").forEach(function (input) {
				input.disabled = !visible;
			});
		});
	}

	document.addEventListener("DOMContentLoaded", function () {
		var select = document.querySelector("[data-person-count]");
		if (!select) {
			return;
		}
		select.addEventListener("change", syncPersonFields);
		syncPersonFields();
	});
})();
