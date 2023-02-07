<#import "common.macro.ftl" as m>
<@m.commonWrapper>
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Dziękujemy za zwrot wypożyczenia. Poniżesz znajdziesz ponownie przeliczone stawki wypożyczonych przez Ciebie
    sprzętów na podstawie daty stworzonego dokumentu zwrotu o numerze <strong>${returnIdentifier}</strong>.
  </p>
  <#include "includes/create-new-return.include.ftl">
  <p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    Dodatkowo w załączniku tej wiadomości znajdziesz fakturę VAT ze szczegółami zwrotu do pobrania w
    formacie PDF.
  </p>
</@m.commonWrapper>
