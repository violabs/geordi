package io.violabs.geordi.exceptions

class SimulationGroupNotFoundException(methodName: String) :
    Exception("No scenarios found for method $methodName")
