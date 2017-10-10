// import "./vendor";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {AppModule} from "./app/app.module";
import {enableProdMode} from "@angular/core";

// import "./polyfills";

if (ENV == "prod") {
    console.log("PROD enabled!");
    enableProdMode();
}
platformBrowserDynamic().bootstrapModule(AppModule);
