import {NgModule} from "@angular/core";
import {PersonComponent} from "./person/person.component";
import {PersonModalComponent} from "./person-modal/person-modal.component";
import {PersonModalService} from "./person-modal/person-modal.service";
import {SharedModule} from "../shared/shared.module";
import {PersonViewComponent} from "./person-view/person-view.component";
import {FileUploadModule} from "ng2-file-upload";
@NgModule({
    imports: [SharedModule, FileUploadModule],
    declarations: [PersonComponent, PersonModalComponent, PersonViewComponent],
    providers: [PersonModalService],
    exports: [PersonComponent, PersonModalComponent, PersonViewComponent]
})
export class PersonModule{

}