package net.alexanderkahn.service.commons.firebaseauth

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.lang.reflect.Parameter

/**
 * Replacement for JUnit 4.x MockitoRunner. Not included in any released packages yet, so reproduced here.
 * See original: https://github.com/junit-team/junit5-samples/blob/master/junit5-mockito-extension/src/main/java/com/example/mockito/MockitoExtension.java
 */
class MockitoExtension : TestInstancePostProcessor, ParameterResolver {

    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        MockitoAnnotations.initMocks(testInstance)
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.isAnnotationPresent(Mock::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return getMock(parameterContext.parameter, extensionContext)
    }

    private fun getMock(parameter: Parameter, extensionContext: ExtensionContext): Any {
        val mockType = parameter.type
        val mocks = extensionContext.getStore(Namespace.create(MockitoExtension::class.java, mockType))
        val mockName = getMockName(parameter)

        return if (mockName != null) {
            mocks.getOrComputeIfAbsent(mockName) { mock(mockType, mockName) }
        } else {
            mocks.getOrComputeIfAbsent(mockType.canonicalName) { mock(mockType) }
        }
    }

    private fun getMockName(parameter: Parameter): String? {
        val explicitMockName = parameter.getAnnotation(Mock::class.java).name.trim()
        if (!explicitMockName.isEmpty()) {
            return explicitMockName
        } else if (parameter.isNamePresent) {
            return parameter.name
        }
        return null
    }

}