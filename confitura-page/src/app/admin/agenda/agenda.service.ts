import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {AgendaEntry} from "./agenda.model";
import {CustomHttp} from "../../shared/custom-http.service";
import {Response} from "@angular/http";
import {Room} from "./room.model";
import {TimeSlot} from "./time-slot.model";
@Injectable()
export class AgendaService {


    constructor(private http: CustomHttp) {
    }

    getAll(): Observable<AgendaEntry[]> {
        return this.http.get('/agenda')
            .map((response: Response) => response.json()["_embedded"]["agendaEntries"] as AgendaEntry[]);
    }

    addEntry(param: { timeSlot: string; room: string; label?: string , presentation?: string}) {
        return this.http.post(`/agenda/`, param);
    }

    getRooms(): Observable<Room[]> {
        return this.http.get('/rooms')
            .map((response: Response) => response.json()["_embedded"]["rooms"] as Room[]);
    }
    getTimeSlots(): Observable<TimeSlot[]> {
        return this.http.get('/time-slots')
            .map((response: Response) => response.json()["_embedded"]["timeSlots"] as TimeSlot[]);
    }

    save(participant: AgendaEntry) {
        return this.http.post(`/agenda/${participant.id}`, participant);
    }

    removeEntry(id: string) {
        return this.http.remove(`/agenda/${id}`);
    }

    addRoom(param: { label: string; displayOrder: number }) {
        return this.http.post(`/rooms/`, param);
    }

    addTimeSlot(param: { label: string; displayOrder: number, forAllRooms: boolean }) {
        return this.http.post(`/time-slots/`, param);
    }

    updateRoom(room: Room) {
        return this.http.post(`/rooms`, room);
    }

    updateTimeSlot(timeslot: TimeSlot) {
        return this.http.post(`/time-slots/`, timeslot);
    }

    removeRoom(id: string) {
        return this.http.remove(`/rooms/${id}`);
    }

    removeTimeSlot(id: string) {
        return this.http.remove(`/time-slots/${id}`);
    }
}
