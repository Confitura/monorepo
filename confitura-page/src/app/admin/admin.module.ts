import {NgModule} from "@angular/core";
import {AdminRoutingModule} from "./admin-routing.module";
import {PartnerEditComponent} from "./partners/partner/partner-edit.component";
import {SharedModule} from "../shared/shared.module";
import {UserListComponent} from "./users/user-list/user-list.component";
import {PersonModule} from "../persons/persons.module";
import {ParticipantListComponent} from "./participants/participant-list.component";
import {FileUploadModule} from "ng2-file-upload";
@NgModule({
    imports: [AdminRoutingModule, SharedModule, PersonModule, FileUploadModule],
    declarations: [PartnerEditComponent, UserListComponent, ParticipantListComponent]
})
export class AdminModule {

}