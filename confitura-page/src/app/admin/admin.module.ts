import {NgModule} from "@angular/core";
import {AdminRoutingModule} from "./admin-routing.module";
import {PartnerEditComponent} from "./partners/partner/partner-edit.component";
import {SharedModule} from "../shared/shared.module";
@NgModule({
    imports: [AdminRoutingModule, SharedModule],
    declarations: [PartnerEditComponent]
})
export class AdminModule {

}