import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {HttpConfiguration} from "./http-configuration.service";
@NgModule({
    providers: [HttpConfiguration],
    imports: [BrowserModule, HttpModule],
    exports: [BrowserModule, HttpModule]
})
export class SharedModule{

}