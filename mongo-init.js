// Script de inicialización de MongoDB
// Se ejecuta automáticamente al crear el contenedor

// Cambiar a la base de datos de la aplicación
db = db.getSiblingDB('enrollment_db');

// Crear usuario para la aplicación
db.createUser({
    user: 'app_user',
    pwd: 'app_password',
    roles: [
        {
            role: 'readWrite',
            db: 'enrollment_db'
        }
    ]
});

// Crear colecciones iniciales
db.createCollection('users');
db.createCollection('roles');
db.createCollection('students');
db.createCollection('courses');
db.createCollection('enrollments');

// Insertar roles básicos
db.roles.insertMany([
    {
        _id: new ObjectId(),
        nombre: 'ADMIN',
    },
    {
        _id: new ObjectId(),
        nombre: 'TEACHER',
    },
    {
        _id: new ObjectId(),
        nombre: 'STUDENT',
    }
]);

// Insertar usuario administrador por defecto
db.users.insertOne({
    _id: new ObjectId(),
    usuario: 'admin',
    contrasena: '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', // admin123
    roles: [db.roles.findOne({nombre: 'ADMIN'})._id],
    estado: true
});

// Crear índices para mejor rendimiento
db.users.createIndex({ "usuario": 1 }, { unique: true });
db.students.createIndex({ "dni": 1 }, { unique: true });
db.courses.createIndex({ "siglas": 1 }, { unique: true });

print('Base de datos inicializada correctamente');
print('Usuario admin creado con contraseña: admin123');
print('Roles básicos creados: ADMIN, TEACHER, STUDENT'); 