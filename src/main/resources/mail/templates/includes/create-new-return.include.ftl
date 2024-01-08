<#include "rent-return-client-details.include.ftl">
<h2 class="h6 text-secondary" style="color: #718096; padding-top: 0; padding-bottom: 0; font-weight: 500;
  vertical-align: baseline; font-size: 16px; line-height: 19.2px; margin: 0;" align="left">
  Szczegóły zwrotu:
</h2>
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
<table class="table" border="0" cellpadding="0" cellspacing="0" style="width: 100%; max-width: 100%;">
  <tbody>
  <tr>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      Data wypożyczenia:
    </td>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      <strong>${data.rentDate?replace("T", ", ")}</strong>
    </td>
  </tr>
  <tr>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      Data zwrotu:
    </td>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      <strong>${data.returnDate?replace("T", ", ")}</strong>
    </td>
  </tr>
  <tr>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      Całkowity czas wypożyczenia:
    </td>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      <strong>${data.rentTime}</strong>
    </td>
  </tr>
  <tr>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      Stawka podatku (VAT):
    </td>
    <td class="lh-sm" style="line-height: 1.25; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
      border-top-style: solid; margin: 0; padding: 12px;" align="left" valign="top">
      <strong>${data.tax}%</strong>
    </td>
  </tr>
  </tbody>
</table>
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
<h2 class="h6 text-secondary" style="color: #718096; padding-top: 0; padding-bottom: 0; font-weight: 500;
  vertical-align: baseline; font-size: 16px; line-height: 19.2px; margin: 0;" align="left">
  Zwrócony sprzęt
</h2>
<#include "rent-return-equipment-details.include.ftl">
