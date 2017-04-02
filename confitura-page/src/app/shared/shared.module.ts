import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {HttpConfiguration} from "./http-configuration.service";
import {CommonModule} from "@angular/common";
import {CustomHttp} from "./custom-http.service";
import {LoginService} from "../security/login.service";
import {CurrentUser} from "../security/current-user.service";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
@NgModule({
    providers: [HttpConfiguration, CustomHttp, LoginService, CurrentUser],
    imports: [CommonModule, HttpModule],
    exports: [CommonModule, HttpModule, FormsModule, RouterModule]
})
export class SharedModule{

}