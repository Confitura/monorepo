import {NgModule} from "@angular/core";
import {routing} from "./profile.routing";
import {SharedModule} from "../shared/shared.module";
import {ProfileEditComponent} from "./profile-edit/profile-edit.component";
import {ProfileViewComponent} from "./profile-view/profile-view.component";
import {PersonModule} from "../persons/persons.module";
import {PresentationEditComponent} from "./presentation-edit/presentation-edit.component";
import {TagInputModule} from "ng2-tag-input";
import {ProfileCompleteGuard} from "./shared/profile-complete-guard.service";
import {SpeakerSelectComponent} from "./speaker-select/speaker.multiselect.component";

@NgModule({
    imports: [routing, SharedModule, PersonModule,TagInputModule],
    declarations: [ProfileEditComponent, ProfileViewComponent, PresentationEditComponent, SpeakerSelectComponent],
    providers: [ProfileCompleteGuard]

})
export class ProfileModule {

}
