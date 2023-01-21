<#import "common.macro.ftl" as m>
<@m.commonWrapper>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Otrzymujesz tą wiadomość, ponieważ właśnie dodałeś nowego pracownika do systemu. Poniżesz znajdziesz jego
    szczegółowe dane oraz login i hasło dostępu do konta pocztowego wygenerowane przez system.
  </p>
  <table class="s-6 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 24px; font-size: 24px; width: 100%; height: 24px; margin: 0;" align="left" width="100%" height="24">&#160;</td></tr></tbody>
  </table>
  <table class="table" border="0" cellpadding="0" cellspacing="0" style="width: 100%; max-width: 100%;">
    <tbody>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Imię i nazwisko:
      </td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.fullName}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Nr PESEL:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.pesel}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Nr telefonu:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.phoneNumber}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Data urodzenia:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.bornDate}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Data zatrudnienia:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.hiredDate}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Adres zamieszkania:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.address}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Płeć pracownika:</td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.gender}</strong>
      </td>
    </tr>
    </tbody>
  </table>
  <table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
  </table>
  <p class="text-danger lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0; color: #dc3545;" align="left">
    <strong>UWAGA!</strong> Tych danych nie podawaj nikomu innemu, oprócz dodawanemu pracownikowi.
  </p>
  <table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
  </table>
  <table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
  </table>
  <table class="table" border="0" cellpadding="0" cellspacing="0" style="width: 100%; max-width: 100%;">
    <tbody>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Adres email:
      </td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.emailAddress}</strong>
      </td>
    </tr>
    <tr>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        Hasło do poczty:
      </td>
      <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
        <strong>${employer.emailPassword}</strong>
      </td>
    </tr>
    </tbody>
  </table>
  <table class="s-5 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
    <tbody><tr><td style="line-height: 20px; font-size: 20px; width: 100%; height: 20px; margin: 0;" align="left" width="100%" height="20">&#160;</td></tr></tbody>
  </table>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Konto pracownika, który nie zaloguje się na nowo stworzone konto do systemu przez następne <strong>72</strong>
    godziny zostanie automatycznie usunięte z systemu.
  </p>
</@m.commonWrapper>
