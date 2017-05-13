import {NgModule} from "@angular/core";
import {AdminRoutingModule} from "./admin-routing.module";
import {PartnerEditComponent} from "./partners/partner/partner-edit.component";
import {SharedModule} from "../shared/shared.module";
import {UserListComponent} from "./users/user-list/user-list.component";
import {PersonModule} from "../persons/persons.module";
@NgModule({
    imports: [AdminRoutingModule, SharedModule, PersonModule],
    declarations: [PartnerEditComponent, UserListComponent]
})
export class AdminModule {

}