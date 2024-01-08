<#import "common.macro.ftl" as m>
<@m.commonWrapper>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Pracownik ${employerFullName} właśnie złożył nowy zwrot zamówienia o numerze <strong>${rentIdentifier}</strong>.
    Poniżesz znajdziesz ponownie przeliczone stawki wypożyczonych sprzętów na podstawie daty stworzonego dokumentu
    zwrotu o numerze <strong>${returnIdentifier}</strong>.
  </p>
    <#include "includes/create-new-return.include.ftl">
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Dodatkowo w załączniku tej wiadomości znajdziesz fakturę VAT ze szczegółami zwrotu do pobrania w
    formacie PDF.
  </p>
  <table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;"
         width="100%">
    <tbody>
    <tr>
      <td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%"
          height="12">&#160;
      </td>
    </tr>
    </tbody>
  </table>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Wszystkie zwroty oraz ich szczegóły możesz znaleźć
    <a href="${baseServletPath}/owner/returns" style="color: #0d6efd;"><u>pod tym adresem</u></a>.
  </p>
</@m.commonWrapper>
