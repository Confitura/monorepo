import {Injectable} from "@angular/core";
import {CustomHttp} from "../../../shared/custom-http.service";
import {Presentation} from "./presentation.model";
import {Response} from "@angular/http";
import {Tag} from "./tag.model";
import {Observable} from "rxjs";
import {CurrentUser} from "../../../security/current-user.service";
@Injectable()
export class PresentationService {
    constructor(private http: CustomHttp, private user: CurrentUser) {
    }

    save(presentation: Presentation): Observable<Response> {
        return this.http.post(`/users/${this.user.get().jti}/presentations`, presentation);
    }

    create(presentation: Presentation): Observable<Response> {
        return this.http.put(`/users/${this.user.get().jti}/presentations`, presentation);
    }

    allTags() {
        return this.http.get("/tags")
            .map((response: Response) => response.json()["_embedded"]["tags"] as object[])
            .map((objects: object[]) => objects.map(value => new Tag(value["id"], value["name"])));
    }

}