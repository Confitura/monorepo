import {NgModule} from "@angular/core";
import {HttpModule} from "@angular/http";
import {HttpConfiguration} from "./http-configuration.service";
import {CommonModule} from "@angular/common";
import {CustomHttp} from "./custom-http.service";
import {LoginService} from "../security/login.service";
import {CurrentUser} from "../security/current-user.service";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {AdminDirective} from "./admin.directive";
import {ImageResizer} from "./ImageResizer.service";
@NgModule({
    imports: [CommonModule, HttpModule],
    declarations: [AdminDirective],
    providers: [HttpConfiguration, CustomHttp, LoginService, CurrentUser, ImageResizer],
    exports: [CommonModule, HttpModule, FormsModule, RouterModule, AdminDirective]
})
export class SharedModule {

}