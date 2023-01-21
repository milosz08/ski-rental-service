<#import "common.macro.ftl" as m>
<@m.commonWrapper>
  <p class="text-gray-700 lh-sm" style="line-height: 1.25; font-size: 16px; color: #4a5568; width: 100%; margin: 0;" align="left">
    Wysłałeś zgłoszenie o reset hasła do Twojego konta. Jeśli w dalszym ciągu chcesz zresetować hasło,
    kliknij w poniższy przycisk. W przeciwnym wypadku możesz zignorować tą wiadomość.
  </p>
  <table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
  </table>
  <table class="s-6 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 24px; font-size: 24px; width: 100%; height: 24px; margin: 0;" align="left" width="100%" height="24">&#160;</td></tr></tbody>
  </table>
  <div class="text-center" style="" align="center">
    <table class="btn btn-dark text-xl" role="presentation" border="0" cellpadding="0" cellspacing="0" style="border-radius: 6px; border-collapse: separate !important; font-size: 20px; line-height: 24px;">
      <tbody>
      <tr>
        <td style="line-height: 24px; font-size: 16px; border-radius: 6px; margin: 0;" align="center" bgcolor="#1a202c">
          <a href="${baseServletPath}/change-forgotten-password?token=${token}" style="color: #ffffff; font-size: 16px; font-family: Helvetica, Arial, sans-serif; text-decoration: none; border-radius: 6px;
          line-height: 20px; display: block; font-weight: normal; white-space: nowrap; background-color: #1a202c; padding: 8px 12px; border: 1px solid #1a202c;">
            Resetuj hasło
          </a>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
  <table class="s-6 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 24px; font-size: 24px; width: 100%; height: 24px; margin: 0;" align="left" width="100%" height="24">&#160;</td></tr></tbody>
  </table>
  <table class="s-4 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 16px; font-size: 16px; width: 100%; height: 16px; margin: 0;" align="left" width="100%" height="16">&#160;</td></tr></tbody>
  </table>
  <p class="text-gray-700 lh-sm" style="line-height: 1.25; font-size: 16px; color: #4a5568; width: 100%; margin: 0;" align="left">
    Link będzie ważny przez <span class="fw-700" style="font-weight: 700 !important;">10 minut</span>. Jeśli do tego czasu nie uda Ci się zmienić hasła,
    <a href="${baseServletPath}/forgot-password-request" style="color: #0d6efd;"><u>kliknij tutaj</u></a> aby wygenerować nowy link.
  </p>
  <table class="s-4 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 16px; font-size: 16px; width: 100%; height: 16px; margin: 0;" align="left" width="100%" height="16">&#160;</td></tr></tbody>
  </table>
</@m.commonWrapper>
