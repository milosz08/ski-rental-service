<#import "common.macro.ftl" as m>
<@m.commonWrapper>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Dziękujemy za złożenie wypożyczenia. Poniżej znajdziesz szczegóły Twojego wypożyczenia o numerze
    <strong>${rentIdentifier}</strong>.
  </p>
    <#include "includes/add-new-rent.include.ftl">
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Dodatkowo w załączniku tej wiadomości znajdziesz fakturę VAT ze szczegółami zamówienia do pobrania w
    formacie PDF.
  </p>
</@m.commonWrapper>
