# Vagrant Repository
This is a Vagrant Repository for private hosting of Vagrant boxes

The Vagrant Repository is comprised of two modules.

## GUI
The GUI modules is the web interface that is used to manage hosted Vagrant Boxes in the repository.

The GUI is a ReactJS based user interface, that allows users to create and maintain groups, boxes, versions and providers.

## API
The API module contains the REST API for the Vagrant Repository.

The API contains GUI endpoints for managing groups, boxes, versions and providers in the given hierarchical order,

It also provides the endpoints used by Vagrant clients to retrieve information about hosted Vagrant boxes.

