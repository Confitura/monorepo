import {NgModule} from "@angular/core";
import {routing} from "./profile.routing";
import {SharedModule} from "../shared/shared.module";
import {ProfileEditComponent} from "./profile-edit/profile-edit.component";
import {ProfileViewComponent} from "./profile-view/profile-view.component";
import {PersonModule} from "../persons/persons.module";

@NgModule({
    imports: [routing, SharedModule, PersonModule],
    declarations: [ProfileEditComponent, ProfileViewComponent]
})
export class ProfileModule {

}