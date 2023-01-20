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
    $(selector).toArray().forEach(function (el) {
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

function onLoad() {

    showModal('logoutModal');
    showHidePassword('.password-input-toggler');

    $('[data-bs-toggle="tooltip"]').toArray().forEach(function(el) {
        new bootstrap.Tooltip(el);
    });

    $('#nav-toggle-button').on('click', function() {
        $('#left-nav-wrapper').toggleClass('left-nav-wrapper-active');
        $('#main-wrapper').toggleClass('main-wrapper-active');
    });
}
disableResize();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

$(window).on('load', onLoad);
