/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: scripts.js
 *  Last modified: 22.12.2022, 20:34
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

const passVisibilityUI = {
    pswVisibilityBtn: document.querySelectorAll('.password-input-toggler'),

    pswVisibilityInvoker: function () {
        if (passVisibilityUI.pswVisibilityBtn === null) return;
        const invokeOnClick = function (el) {
            const inputChild = el.parentNode.firstElementChild;
            const buttonIcon = el.parentNode.children[1].firstElementChild;
            el.addEventListener('click', function () {
                if (inputChild.value.length === 0) return;
                inputChild.type = inputChild.type === 'text' ? 'password' : 'text';
                if (buttonIcon.classList.contains('bi-eye-slash-fill')) {
                    buttonIcon.classList.remove('bi-eye-slash-fill');
                    buttonIcon.classList.add('bi-eye-fill');
                } else {
                    buttonIcon.classList.remove('bi-eye-fill');
                    buttonIcon.classList.add('bi-eye-slash-fill');
                }
            });
            inputChild.addEventListener('input', function () {
                if (this.value !== '') return;
                inputChild.type = 'password';
                buttonIcon.classList.remove('bi-eye-slash-fill');
                buttonIcon.classList.add('bi-eye-fill');
            });
        };
        this.pswVisibilityBtn.forEach(invokeOnClick.bind(this), false);
    },
};

window.addEventListener('load', function() {
    passVisibilityUI.pswVisibilityInvoker();
});
