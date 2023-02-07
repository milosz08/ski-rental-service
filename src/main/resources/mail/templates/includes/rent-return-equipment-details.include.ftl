<table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
</table>
<table class="hr" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
  <tbody><tr><td style="line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0; border-top-style: solid;
    height: 1px; width: 100%; margin: 0;" align="left"></td></tr></tbody>
</table>
<table class="s-2 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 8px; font-size: 8px; width: 100%; height: 8px; margin: 0;" align="left" width="100%" height="8">&#160;</td></tr></tbody>
</table>
<#list data.rentEquipments as equipment>
  <div class="row" style="margin-right: -24px;">
    <table class="" role="presentation" border="0" cellpadding="0" cellspacing="0" style="table-layout: fixed; width: 100%;" width="100%">
      <tbody>
      <tr>
        <td class="col" style="line-height: 24px; font-size: 16px; min-height: 1px; font-weight: normal; padding-right: 24px; margin: 0;" align="left" valign="top">
          <p style="line-height: 24px; font-size: 16px; width: 100%; margin: 0;" align="left">
              ${equipment.count}x <strong>${equipment.name}</strong>
          </p>
          <p class="text-sm" style="line-height: 16.8px; font-size: 14px; width: 100%; margin: 0;" align="left">
            Nr katalogowy <strong>${equipment.typeAndModel}</strong>
          </p>
          <table class="s-2 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
            <tbody><tr><td style="line-height: 8px; font-size: 8px; width: 100%; height: 8px; margin: 0;" align="left" width="100%" height="8">&#160;</td></tr></tbody>
          </table>
          <p class="text-sm text-muted" style="line-height: 16.8px; font-size: 14px; color: #718096; width: 100%; margin: 0;" align="left">
            Dodatkowa kaucja:
          </p>
        </td>
        <td class="col-3" style="line-height: 24px; font-size: 16px; min-height: 1px; font-weight: normal; padding-right: 24px; width: 25%; margin: 0;" align="left" valign="top">
          <p class="fw-600 text-right" style="line-height: 24px; font-size: 16px; font-weight: 600 !important; width: 100%; margin: 0;" align="right">
              ${equipment.totalPriceBrutto?string.currency}
          </p>
          <p class="text-sm text-right" style="line-height: 16.8px; font-size: 14px; width: 100%; margin: 0;" align="right">
            (netto) ${equipment.totalPriceNetto?string.currency}
          </p>
          <table class="s-2 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
            <tbody><tr><td style="line-height: 8px; font-size: 8px; width: 100%; height: 8px; margin: 0;" align="left" width="100%" height="8">&#160;</td></tr></tbody>
          </table>
          <p class="text-sm text-muted text-right" style="line-height: 16.8px; font-size: 14px; color: #718096; width: 100%; margin: 0;" align="right">
            + ${equipment.depositPriceBrutto?string.currency}
          </p>
          <p class="text-sm text-muted text-right" style="line-height: 16.8px; font-size: 14px; color: #718096; width: 100%; margin: 0;" align="right">
            + (netto) ${equipment.depositPriceNetto?string.currency}
          </p>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</#list>
<table class="s-4 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 16px; font-size: 16px; width: 100%; height: 16px; margin: 0;" align="left" width="100%" height="16">&#160;</td></tr></tbody>
</table>
<table class="hr" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
  <tbody><tr><td style="line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0; border-top-style: solid;
    height: 1px; width: 100%; margin: 0;" align="left"></td></tr></tbody>
</table>
<table class="s-4 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 16px; font-size: 16px; width: 100%; height: 16px; margin: 0;" align="left" width="100%" height="16">&#160;</td></tr></tbody>
</table>
<p class="fw-600 text-right" style="line-height: 24px; font-size: 16px; font-weight: 600 !important; width: 100%; margin: 0;" align="right">
  Cena całkowita: ${data.totalPriceBrutto?string.currency}
</p>
<table class="s-0 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 0; font-size: 0; width: 100%; height: 0; margin: 0;" align="left" width="100%" height="0">&#160;</td></tr></tbody>
</table>
<p class="text-sm text-muted text-right" style="line-height: 16.8px; font-size: 14px; color: #718096; width: 100%; margin: 0;" align="right">
  (netto) ${data.totalPriceNetto?string.currency}
</p>
<table class="s-2 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 8px; font-size: 8px; width: 100%; height: 8px; margin: 0;" align="left" width="100%" height="8">&#160;</td></tr></tbody>
</table>
<p class="text-sm text-muted text-right" style="line-height: 16.8px; font-size: 14px; color: #718096; width: 100%; margin: 0;" align="right">
  + kaucja ${data.totalDepositPriceBrutto?string.currency}
</p>
<table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
</table>
<p class="fw-600 h5 text-right" style="line-height: 24px; font-size: 20px; padding-top: 0; padding-bottom: 0;
  font-weight: 600 !important; vertical-align: baseline; width: 100%; margin: 0;" align="right">
  Razem: ${data.totalPriceWithDepositBrutto?string.currency}
</p>
<table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
</table>
<table class="hr" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
  <tbody><tr><td style="line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e2e8f0;
    border-top-style: solid; height: 1px; width: 100%; margin: 0;" align="left"></td></tr></tbody>
</table>
<table class="s-4 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 16px; font-size: 16px; width: 100%; height: 16px; margin: 0;" align="left" width="100%" height="16">&#160;</td></tr></tbody>
</table>
<h2 class="h6 text-secondary" style="color: #718096; padding-top: 0; padding-bottom: 0; font-weight: 500;
vertical-align: baseline; font-size: 16px; line-height: 19.2px; margin: 0;" align="left">
  Dodatkowe uwagi:
</h2>
<table class="s-1 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 4px; font-size: 4px; width: 100%; height: 4px; margin: 0;" align="left" width="100%" height="4">&#160;</td></tr></tbody>
</table>
<p class="lh-sm" style="line-height: 1.25; font-size: 16px; width: 100%; margin: 0;" align="left">
    ${additionalDescription}
</p>
<table class="s-3 w-full" role="presentation" border="0" cellpadding="0" cellspacing="0" style="width: 100%;" width="100%">
  <tbody><tr><td style="line-height: 12px; font-size: 12px; width: 100%; height: 12px; margin: 0;" align="left" width="100%" height="12">&#160;</td></tr></tbody>
</table>
