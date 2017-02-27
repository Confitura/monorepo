import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {ProfileComponent} from "./profile.component";
import {FormsModule} from "@angular/forms";
import {UserService} from "./user.service";
@NgModule({
    imports: [SharedModule, FormsModule],
    declarations: [ProfileComponent],
    providers: [UserService]
})
export class ProfileModule {

}