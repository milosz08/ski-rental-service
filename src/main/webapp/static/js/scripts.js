/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: scripts.js
 *  Last modified: 28/12/2022, 23:29
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function showHidePassword(selector) {
    $(selector).each(function (_, el) {
        const input = $(el).parent().find('>:first-child');
        const buttonIcon = $(el).find('>:first-child');
        $(el).on('click', function () {
            if (input.val() === '') return;
            buttonIcon.toggleClass('bi-eye-fill bi-eye-slash-fill');
            if (buttonIcon.hasClass('bi-eye-slash-fill')) {
                input.attr('type', 'text');
            } else {
                input.attr('type', 'password');
            }
        });
        input.on('input', function() {
            if (input.val() !== '') return;
            input.attr('type', 'password');
            buttonIcon.toggleClass('bi-eye-fill bi-eye-slash-fill');
        });
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function showModal(selector) {
    const modal = document.getElementById(selector);
    if (modal !== null) new bootstrap.Modal(modal, {}).show();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function disableResize() {
    let resizeTimer;
    $(window).on('resize', function() {
        $('body').addClass('disable-animations');
        if ($(document).width() > 992) $('[data-bs-dismiss="offcanvas"]').click();
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(() => {
            $('body').removeClass('disable-animations');
        }, 400);
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function triggeredOpenModalImmediately(modalId, attrName) {
    const modalElement = document.getElementById(modalId);
    if (modalElement === null) return;
    if (modalElement.getAttribute('data-equipment-' + attrName + '-modal-enable') === 'open') {
        showModal(modalId);
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function triggeredOpenAddEditCartModalImmediately(addEdit) {
    $('.eq-modals').each(function (_, el) {
        if (el.getAttribute('data-equipment-' + addEdit + '-cart-modal-enable-' + el.getAttribute('data-eqid')) === 'open') {
            showModal(addEdit + 'Equipment' + el.getAttribute('data-eqid'));
        }
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function setMaxElementsOnEveryInput() {
    $('input[type="number"]').on('input', function() {
        if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function onLoad() {
    showModal('logoutModal');
    setMaxElementsOnEveryInput();

    showHidePassword('.password-input-toggler');
    disableResize();

    document.querySelectorAll('.onlyPreTimeSelect').forEach(function(el) {
        el.max = new Date().toISOString().split("T")[0]
    });
    document.querySelectorAll('.onlyNextTimeSelect').forEach(function(el) {
        el.min = new Date().toISOString().slice(0, new Date().toISOString().lastIndexOf(":"));
    });

    $('[data-bs-toggle="tooltip"]').toArray().forEach(function(el) {
        new bootstrap.Tooltip(el);
    });

    $('#nav-toggle-button').on('click', function() {
        $('#left-nav-wrapper').toggleClass('left-nav-wrapper-active');
        $('#main-wrapper').toggleClass('main-wrapper-active');
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

triggeredOpenModalImmediately('equipmentTypeModal', 'type');
triggeredOpenModalImmediately('equipmentBrandModal', 'brand');
triggeredOpenModalImmediately('equipmentColorModal', 'color');
triggeredOpenAddEditCartModalImmediately('add');
triggeredOpenAddEditCartModalImmediately('edit');

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

$(window).on('load', onLoad);
