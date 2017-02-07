import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
@NgModule({
    imports: [BrowserModule, HttpModule],
    exports: [BrowserModule, HttpModule]
})
export class SharedModule{

}