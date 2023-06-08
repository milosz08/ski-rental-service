/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RentEquipmentsDetailsResDto.java
 * Last modified: 2/21/23, 12:44 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.dto.rent;

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record RentEquipmentsDetailsResDto(
    Long id,
    String name,
    Integer count,
    String barcode,
    String description,
    BigDecimal priceNetto,
    BigDecimal priceBrutto,
    BigDecimal depositPriceNetto,
    BigDecimal depositPriceBrutto
) {
}
