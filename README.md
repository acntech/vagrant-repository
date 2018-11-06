# Vagrant Repository
This is a Vagrant Repository for private hosting of Vagrant boxes

Find the code in the GitHub repository:

* [https://github.com/acntech/vagrant-repository](https://github.com/acntech/vagrant-repository)

## Components
The Vagrant Repository is comprised of two components.

### GUI
The GUI component is the web interface that is used to manage hosted Vagrant Boxes in the repository.

The GUI is a ReactJS based user interface, that allows users to create and maintain groups, boxes, versions and providers.

### API
The API component contains the REST API for the Vagrant Repository.

The API contains GUI endpoints for managing groups, boxes, versions and providers in the given hierarchical order,

It also provides the endpoints used by Vagrant clients to retrieve information about hosted Vagrant boxes.

## Getting started
The Vagrant Repository components are available as Docker containers in the Docker Hub:

* [vagrant-repository-gui](https://hub.docker.com/r/acntechie/vagrant-repository-gui)
* [vagrant-repository-api](https://hub.docker.com/r/acntechie/vagrant-repository-api)

### Runtime
Use the following ```docker-compose.yml``` file to run the Vagrant Repository:

```yaml
version: '3.5'

services:
  gui:
    image: vagrant-repository-gui
    container_name: vagrant-repository-gui
    depends_on:
      - api
    environment:
      - API_URL=http://api:8080
    ports:
      - 80:80
    networks:
      - vagrant_repository

  api:
    image: vagrant-repository-api
    container_name: vagrant-repository-api
    networks:
      - vagrant_repository

networks:
  vagrant_repository:
    name: vagrant-repository
```

### Hosting boxes
Vagrant boxes are organized into groups. A box can exist as one or more version. A box version is published with an assosiated provider, which is the virtualization platform that the box is meant to run on. As of now VirtualBox is the only supported provider.

To use a hosted box like e.g. ```acntech/ubuntu``` in Vagrant then define a ```Vagrantfile``` similar to the example below:

```vagrantfile
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|

  # Name of Vagrant Box
  config.vm.box = "acntech/ubuntu"

  # URL to Vagrant Repository for given Vagrant Box
  config.vm.box_url = "http://repo.private.com/api/vagrant/boxes/acntech/ubuntu"

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
