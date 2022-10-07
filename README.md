# Vagrant Repository
This is a Vagrant Repository for private hosting of Vagrant boxes

Find the code in the GitHub repository:

* [https://github.com/acntech/vagrant-repository](https://github.com/acntech/vagrant-repository)

### API
The application has an API that contains a set of REST endpoints for managing hosted Vagrant Boxes in the repository.
This means read and write operations for organizations, boxes, versions and providers for the repository.

It also provides the endpoints used by Vagrant clients to retrieve information about hosted Vagrant boxes.

### GUI
The application also has a GUI that can be used to manage hosted Vagrant boxes in the repository.

## Getting started
The Vagrant Repository are available as a Docker container in the Docker Hub:

* [https://hub.docker.com/r/acntechie/vagrant-repository](https://hub.docker.com/r/acntechie/vagrant-repository)

### Database
The application needs a database in order to run. PostgreSQL is the default database used.

If you want to use another database type you need to migrate the database schema to the SQL syntax of the database
you want to use. The library containing the JDBC driver of the database also needs to be added to the classpath
of the application.

E.g. if you want to use MariaDB instead of PostgreSQL then you create a new directory:
```bash
src/main/resources/db/vendor/mariadb
```
Then you migrate the PostgreSQL script to MariaDB SQL syntax and place it in the directory.

You need to run the application or Docker container with the following environment variables:
```env
SPRING_SQL_INIT_PLATFORM=mariadb
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver
```
Here the platform name `mariadb` must correspond to the directory name created in the previous step.

### Runtime
Use the following ```docker-compose.yml``` file to run the Vagrant Repository:

```yaml
version: "3.7"

services:
  repository:
    image: acntechie/vagrant-repository
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/vagrant_repository
      - SPRING_DATASOURCE_USERNAME=vagrant_repository
      - SPRING_DATASOURCE_PASSWORD=64nd4lf
    networks:
      - postgres
  postgres:
    image: postgres
    environment:
      - POSTGRES_DB=vagrant_repository
      - POSTGRES_USER=vagrant_repository
      - POSTGRES_PASSWORD=64nd4lf
    networks:
      - postgres

networks:
  postgres:
    name: acntech.postgres
```

### Hosting boxes
Vagrant boxes belong to an organization. A box can exist as one or more version.
A box version is published with an associated provider.
The provider is the virtualization platform that the box is meant to run on.
As of now VirtualBox, VMWare and Hyper-V are the only supported provider.

To use a hosted box like e.g. ```acntech/ubuntu``` in Vagrant then define a ```Vagrantfile``` similar to the example below:

```ruby
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|

  # Name of Vagrant Box
  config.vm.box = "acntech/ubuntu"

  # URL to Vagrant Repository for given Vagrant Box
  config.vm.box_url = "http://repo.private.com/api/box/acntech/ubuntu"

  # Don't generate new SSH key, but use the default insecure key
  config.ssh.insert_key = false

  config.vm.provider "virtualbox" do |vb|

    # Name to display in VirtualBox
    vb.name = "AcnTech Ubuntu"

    # Display the VirtualBox GUI when booting the machine
    vb.gui = true

    # Customize the amount of memory on the VM
    vb.memory = "1024"

    # Customize CPU count
    vb.cpus = "1"
  end
end
```
