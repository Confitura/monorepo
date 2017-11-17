export class Person {
  firstName: string;
  lastName: string;
  bio: string;
  photo: string;
  twitter: string;
  url: string;
  presentations: String[];

  get fullName() {
    return `${this.firstName} ${this.lastName}`;
  }
}
