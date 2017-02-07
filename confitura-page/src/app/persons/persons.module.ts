import {NgModule} from "@angular/core";
import {PersonComponent} from "./person/person.component";
import {PersonModalComponent} from "./person-modal/person-modal.component";
import {PersonModalService} from "./person-modal/person-modal.service";
import {SharedModule} from "../shared/shared.module";
@NgModule({
    imports: [SharedModule],
    declarations: [PersonComponent, PersonModalComponent],
    providers: [PersonModalService],
    exports: [PersonComponent, PersonModalComponent]
})
export class PersonModule{

}