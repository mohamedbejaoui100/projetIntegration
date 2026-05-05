amal benjamaa admin123 amal@ieee.org Admin

mohamed bejaoui 12345678 mohamedbejaoui@ieee.org teacher



**1/ pour sincrire**



POST http://localhost:8081/api/auth/register

Body → raw → JSON :

{

&#x20; "nom": "Admin Principal",

&#x20; "email": "admin@edu3d.com",

&#x20; "password": "admin123",

&#x20; "role": "ADMIN"

}



**2/ pour login**

&#x20;

POST http://localhost:8081/api/auth/login

Body → raw → JSON :

{

&#x20; "email": "amal@ieee.org",

&#x20; "password": "admin123"

}



**3/pour voir les user (seul admin peut apres login in)**



GET http://localhost:8081/api/users

Headers: Authorization: Bearer <token\_ADMIN>





**4/pour modifier**



PATCH http://localhost:8081/api/users/1

Authorization: Bearer <token\_admin>

Body → raw → JSON :

{

&#x20; "email": "nouveauemail@test.com"

}



**4/pour suprrimer**



DELETE http://localhost:8081/api/users/2

Headers: Authorization: Bearer <token\_admin>

→ Réponse : 204 No Content





**Quand tu modifies :**



git add .

git commit -m "update"

git push



**Front :**

cd frontend-edu3d

npm run dev



