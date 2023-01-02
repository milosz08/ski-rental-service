<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "https://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <b>Witaj ${fullName}!</b>
        <br>
        <br>
        Aby zresetować hasło, kliknij <a href="${baseServletPath}/change-forgotten-password?token=${token}">tutaj</a>.
        <hr>
        Link aktywacyjny będzie ważny do ${expiredToken} (10 minut). Po tym czasie należy wygenerować nowy link.<br>
        Aby wygenerować nowy link, kliknij <a href="${baseServletPath}/forgot-password-request">tutaj</a>
        <hr>
        <i>Wiadomość wygenerowana automatycznie. Prosimy nie odpowiadać.</i>
        <br>
        <i>SkiRent Service System, ${currentDate}</i>
    </body>
</html>
